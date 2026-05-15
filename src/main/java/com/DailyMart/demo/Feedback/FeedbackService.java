package com.DailyMart.demo.Feedback;

import org.springframework.stereotype.Service;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackService {
    private final String FILE_PATH = "feedbacks.txt";

    // Save one feedback entry to the file
    public void saveFeedback(Feedback feedback) throws IOException {
        // Replace commas in message with a safe character to avoid CSV issues
        String safeMessage = feedback.getMessage().replace(",", ";");
        String line = LocalDateTime.now() + "," +
                feedback.getName() + "," +
                feedback.getEmail() + "," +
                feedback.getRating() + "," +
                safeMessage;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(line);
            writer.newLine();
        }
    }

    // Read all feedbacks from the file
    public List<Feedback> getAllFeedbacks() throws IOException {
        List<Feedback> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Format: timestamp,name,email,rating,message
                String[] parts = line.split(",", 5);  // limit=5 keeps message intact
                if (parts.length == 5) {
                    Feedback f = new Feedback(parts[1], parts[2], parts[3], parts[4]);
                    list.add(f);
                }
            }
        }
        return list;
    }
}
