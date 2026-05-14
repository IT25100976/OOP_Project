package com.DailyMart.demo.AdminManagement;

import com.DailyMart.demo.UserManagement.FileService;
import com.DailyMart.demo.UserManagement.User;
import com.DailyMart.demo.order.model.Order;
import com.DailyMart.demo.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class AdminService {
    private final String ADMIN_CRED_FILE = "admin_credentials.txt";
    private final String ORDERS_FILE = "orders.txt";

    @Autowired
    private FileService userFileService;

    @Autowired
    private OrderRepository orderRepository;

    public AdminService() {
        // Initialize default admin credentials if the file doesn't exist
        File file = new File(ADMIN_CRED_FILE);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_CRED_FILE))) {
                writer.write("admin@dailymart.com,admin123");
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public AdminCredentials getAdminCredentials() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_CRED_FILE))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    return new AdminCredentials(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AdminCredentials("admin@dailymart.com", "admin123"); // fallback
    }

    public boolean updateAdminCredentials(String newUsername, String newPassword) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_CRED_FILE, false))) {
            writer.write(newUsername + "," + newPassword);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateLogin(String username, String password) {
        AdminCredentials creds = getAdminCredentials();
        return creds.getUsername().equalsIgnoreCase(username) && creds.getPassword().equals(password);
    }

    // --- User Management ---

    public List<User> getAllUsers() {
        try {
            return userFileService.getAllUsers();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteUser(String email) {
        try {
            List<User> users = userFileService.getAllUsers();
            boolean removed = users.removeIf(u -> u.getEmail().equalsIgnoreCase(email));
            if (removed) {
                userFileService.saveAllUsers(users);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- Order Management ---

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public boolean deleteOrder(String orderId) {
        List<Order> orders = orderRepository.getAllOrders();
        boolean removed = orders.removeIf(o -> o.getOrderId().equals(orderId));
        
        if (removed) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ORDERS_FILE, false))) {
                for (Order o : orders) {
                    bw.write(o.toFileString());
                    bw.newLine();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
