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
        // @RequestBody User user :: The browser sends a JSON object (firstName, lastName, email, password)
        // Spring Boot automatically converts it into a User object and passes it here as 'user'
        try {
            // Step 1: Load all existing users from users.txt into a Java List
            // This lets us search through them to check for duplicates
            List<User> allUsers = fileService.getAllUsers();

            // Step 2: Check if any existing user has the same email as the new registration
            // .equalsIgnoreCase() makes the check case-insensitive (e.g. "User@Mail.com" == "user@mail.com")
            boolean emailExists = allUsers.stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));

            // Step 3: If the email is already taken, reject the signup with a 409 Conflict status
            // The frontend will read this error message and display it to the user
            if (emailExists) {
                return ResponseEntity.status(409).body("An account with this email already exists.");
            }

            // Step 4: Email is unique — save the new user to users.txt
            fileService.saveUser(user);

            // Step 5: Return a success message; the frontend will redirect the user to login.html
            return ResponseEntity.ok("Account created successfully!");
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
