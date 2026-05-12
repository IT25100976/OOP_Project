package com.DailyMart.demo.UserManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allows the HTML file to talk to the Java backend
public class UserController {

    @Autowired
    private FileService fileService;

    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            fileService.saveUser(user);
            return ResponseEntity.ok("Success! User saved to text file.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    //
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginAttempt) {
        try {
            List<User> allUsers = fileService.getAllUsers();

            // Search the list for a user with matching email and password
            boolean isValid = allUsers.stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(loginAttempt.getEmail()) &&
                            u.getPassword().equals(loginAttempt.getPassword()));

            if (isValid) {
                return ResponseEntity.ok("Login Successful! Welcome back.");
            } else {
                return ResponseEntity.status(401).body("Invalid email or password.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error reading database: " + e.getMessage());
        }
    }

    //allow users to set their own password during a reset

    //@PostMapping("/reset-password"):: when someone send data to /api/reset-password URL tells spring boot to run this method
    //@RequestBody User request :: this contains the email and new password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody User request) {
        try {
            List<User> allUsers = fileService.getAllUsers(); //take everyone into a java list from the file
            boolean userFound = false;

            for (User u : allUsers) {
                // Find the user by email
                if (u.getEmail().equalsIgnoreCase(request.getEmail())) {//request.getEmail() :: when user clicks the reset password and input data their browser send a package of infor to your backend
                    //in my code pakage is caught by variable request.(it comes from @RequestBody)

                    // Update: Use the password provided by the user in the form
                    u.setPassword(request.getPassword());//program goes to the request object then call the getPassword() to grab the new password
                    //now with the existing password get replace with grabbed password by u.setPassword method
                    //here the password only changed in Ram memo..thats wht i call fileService.saveAllUsers(allUsers)
                    userFound = true; //once you found the user email in the java list we stop searching
                    break;
                }
            }

            if (userFound) {
                fileService.saveAllUsers(allUsers); // Save the updated list back to users.txt
                return ResponseEntity.ok("Success! Your password has been updated.");
            } else {
                return ResponseEntity.status(404).body("Email not found in our records.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
