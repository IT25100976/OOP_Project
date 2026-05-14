package com.DailyMart.demo.order.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a complete customer order.
 */
public class   Order {
    private String orderId;
    private String customerName;
    private String email;
    private String address;
    private double totalAmount;
    private String status; // Pending, Shipped, Delivered, Canceled
    private String date;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {}

    public Order(String orderId, String customerName, String email, String address, double totalAmount, String status, String date, List<OrderItem> items) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.email = email;
        this.address = address;
        this.totalAmount = totalAmount;
        this.status = status;
        this.date = date;
        this.items = items;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    /**
     * Serializes items for file storage.
     * Format: productId:qty:unitPrice|...
     */
    public String serializeItems() {
        return items.stream()
                .map(item -> String.format("%s:%d:%.2f", item.getProductId(), item.getQuantity(), item.getUnitPrice()))
                .collect(Collectors.joining("|"));
    }

    /**
     * CSV format for orders.txt:
     * OrderID,CustomerName,Email,Address,TotalAmount,Status,Date,ItemsSerialized
     */
    public String toFileString() {
        return String.format("%s,%s,%s,%s,%.2f,%s,%s,%s",
                orderId, customerName, email, address.replace(",", ";"), totalAmount, status, date, serializeItems());
    }
}
