package com.DailyMart.demo.AdminManagement;

import com.DailyMart.demo.UserManagement.User;
import com.DailyMart.demo.order.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AdminCredentials credentials) {
        if (adminService.validateLogin(credentials.getUsername(), credentials.getPassword())) {
            return ResponseEntity.ok("Login Successful");
        } else {
            return ResponseEntity.status(401).body("Invalid admin username or password.");
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody AdminCredentials credentials) {
        boolean success = adminService.updateAdminCredentials(credentials.getUsername(), credentials.getPassword());
        if (success) {
            return ResponseEntity.ok("Admin credentials updated successfully.");
        } else {
            return ResponseEntity.internalServerError().body("Failed to update credentials.");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        if (users != null) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/users/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        boolean success = adminService.deleteUser(email);
        if (success) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("User not found or failed to delete.");
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = adminService.getAllOrders();
        if (orders != null) {
            return ResponseEntity.ok(orders);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable String orderId) {
        boolean success = adminService.deleteOrder(orderId);
        if (success) {
            return ResponseEntity.ok("Order deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Order not found or failed to delete.");
        }
    }
}
