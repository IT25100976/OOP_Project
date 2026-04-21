package com.DailyMart.demo.Signup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.io.IOException;

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
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody User request) {
        try {
            List<User> allUsers = fileService.getAllUsers();
            boolean userFound = false;

            for (User u : allUsers) {
                // Find the user by email
                if (u.getEmail().equalsIgnoreCase(request.getEmail())) {
                    // Update: Use the password provided by the user in the form
                    u.setPassword(request.getPassword());
                    userFound = true;
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
