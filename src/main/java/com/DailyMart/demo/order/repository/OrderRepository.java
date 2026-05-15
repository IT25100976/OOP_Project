package com.DailyMart.demo.order.repository;

import com.DailyMart.demo.order.model.Order;
import com.DailyMart.demo.order.model.OrderItem;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing orders in orders.txt.
 */
@Repository
public class OrderRepository {
    private static final String FILE_PATH = "orders.txt";

    public OrderRepository() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * READ: Loads all orders from orders.txt.
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 8) {
                    Order order = new Order();
                    order.setOrderId(data[0]);
                    order.setCustomerName(data[1]);
                    order.setEmail(data[2]);
                    order.setAddress(data[3].replace(";", ","));
                    order.setTotalAmount(Double.parseDouble(data[4]));
                    order.setStatus(data[5]);
                    order.setDate(data[6]);
                    
                    // Parse Items: productId:productName:qty:unitPrice|...
                    String itemsStr = data[7];
                    List<OrderItem> items = Arrays.stream(itemsStr.split("\\|"))
                            .map(s -> {
                                String[] itemData = s.split(":");
                                if (itemData.length >= 4) {
                                    // New format: productId:productName:qty:unitPrice
                                    return new OrderItem(itemData[0], itemData[1].replace(";", ":"), Double.parseDouble(itemData[3]), Integer.parseInt(itemData[2]));
                                } else {
                                    // Legacy format: productId:qty:unitPrice
                                    // Fallback to "Product" or actual name if we can find it (for now just use "Product (ID)")
                                    String fallbackName = "Product (" + itemData[0] + ")";
                                    return new OrderItem(itemData[0], fallbackName, Double.parseDouble(itemData[2]), Integer.parseInt(itemData[1]));
                                }
                            })
                            .collect(Collectors.toList());
                    order.setItems(items);
                    orders.add(order);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * CREATE: Adds a new order to the file.
     */
    public void addOrder(Order order) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(order.toFileString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * UPDATE: Updates the status of an order.
     */
    public void updateOrderStatus(String orderId, String newStatus) {
        List<Order> orders = getAllOrders();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Order o : orders) {
                if (o.getOrderId().equals(orderId)) {
                    o.setStatus(newStatus);
                }
                bw.write(o.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
