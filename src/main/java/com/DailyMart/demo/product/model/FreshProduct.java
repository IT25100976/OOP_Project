package com.DailyMart.demo.product.model;

/**
 * Concrete implementation of a product sold by weight (e.g., Vegetables).
 * Inherits from the Product base class.
 */
public class FreshProduct extends Product {
    private double weight;

    public FreshProduct() {}

    public FreshProduct(String id, String name, String category, double price, int stock, double weight) {
        super(id, name, category, price, stock);
        this.weight = weight;
    }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    /**
     * Compiles product data into a format saved in products.txt:
     * ID,Name,Price,Stock,Category,Weight
     */
    @Override
    public String toFileString() {
        return String.format("%s,%s,%s,%s,%s,%s", getId(), getName(), getPrice(), getStock(), getCategory(), getWeight());
    }

    @Override
    public String displayDetails() {
        return String.format("Fresh Product: %s, Price: Rs %.2f, Stock: %d, Weight: %.2f kg", getName(), getPrice(), getStock(), weight);
    }
}
