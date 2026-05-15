package com.DailyMart.demo.product.model;

/**
 * Concrete implementation of a pre-packaged product with an expiry date.
 * Inherits from the Product base class.
 */
public class PackagedProduct extends Product {
    private String expiryDate;

    public PackagedProduct() {}

    public PackagedProduct(String id, String name, String category, double price, int stock, String imageUrl, String expiryDate) {
        super(id, name, category, price, stock, imageUrl);
        this.expiryDate = expiryDate;
    }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    /**
     * Compiles product data into a format saved in products.txt:
     * ID,Name,Price,Stock,Category,ImageUrl,ExpiryDate
     */
    @Override
    public String toFileString() {
        return String.format("%s|%s|%s|%s|%s|%s|%s", 
                getId(), getName(), getPrice(), getStock(), getCategory(), 
                (getImageUrl() == null || getImageUrl().isEmpty() ? " " : getImageUrl()), 
                getExpiryDate());
    }

    @Override
    public String displayDetails() {
        return String.format("Packaged Product: %s, Price: Rs %.2f, Stock: %d, Expiry Date: %s", getName(), getPrice(), getStock(), expiryDate);
    }
}
