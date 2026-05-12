package com.DailyMart.demo.product.model;

/**
 * Abstract Base Class for all Products in the system.
 * Uses OOP principles like Encapsulation (private fields) and Abstraction (abstract methods).
 */
public abstract class Product {
    private String id;
    private String name;
    private String category;
    private double price;
    private int stock;

    public Product() {}

    public Product(String id, String name, String category, double price, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    // Standard Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    /**
     * Formats the product data into a CSV string for flat-file storage.
     */
    public abstract String toFileString();

    /**
     * Formats the product data for user-friendly console or log display.
     */
    public abstract String displayDetails();
}
