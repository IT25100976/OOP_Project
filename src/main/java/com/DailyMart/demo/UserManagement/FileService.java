package com.DailyMart.demo.UserManagement;
//This class handles the "Database" logic (writing to the text file)
import org.springframework.stereotype.Service;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

//packages that necessary to read text file
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    private final String FILE_PATH = "users.txt";

    public void saveUser(User user) throws IOException {
        // Construct a line: firstName,lastName,email,password like wise
        String userData = user.getFirstName() + "," +
                user.getLastName() + "," +
                user.getEmail() + "," +
                user.getPassword();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(userData);
            writer.newLine();
        }
    }
    // NEW: Method to read all users from the text file
    public List<User> getAllUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // Assuming format: firstName,lastName,email,password
                if (data.length == 4) {
                    users.add(new User(data[0], data[1], data[2], data[3]));
                }
            }
        }
        return users;
    }

    //this method for reset password in UserControll
    public void saveAllUsers(List<User> users) throws IOException {
        // The 'false' here is key: it tells Java to wipe the file and start fresh
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (User u : users) {
                String userData = u.getFirstName() + "," +
                        u.getLastName() + "," +
                        u.getEmail() + "," +
                        u.getPassword();
                writer.write(userData);
                writer.newLine();
            }
        }
    }
}
