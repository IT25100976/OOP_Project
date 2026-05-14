package com.DailyMart.demo.InventoryManagement;

// =============================================================================
// ProductItem.java
// PACKAGE: com.DailyMart.demo.InventoryManagement
//
// PURPOSE: This is the MODEL (data class) for a single product that exists
//          in either the wishlist or the cart.
//
// TEXT FILE FORMAT (pipe-separated to avoid conflicts with product names):
//   sessionId|productId|productName|price|quantity|imageUrl
//
// EXAMPLE LINE IN wishlist_db.txt or cart_db.txt:
//   abc123|P001|Organic Apple|168.0|1|./assets/images/product/Apple-first.png
// =============================================================================

public class ProductItem {

    // --- Fields ---
    private String id;        // Unique product ID (e.g., "P001")
    private String name;      // Display name of the product
    private double price;     // Price per unit
    private int quantity;     // How many units (mainly used in cart)
    private String imageUrl;  // Relative path to the product image

    // --- Constructors ---

    // Empty constructor required by Spring's JSON deserializer (Jackson)
    // When the frontend sends JSON in the request body, Jackson calls this first
    public ProductItem() {
    }

    // Full constructor for creating items programmatically
    public ProductItem(String id, String name, double price, int quantity, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // --- Getters and Setters ---
    // These are required by Jackson to serialize Java objects → JSON responses

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // --- Text File Serialization ---

    /**
     * Converts this ProductItem into a single text-file line.
     * We use PIPE (|) as the separator instead of comma, because product names
     * and image URLs can contain commas which would break CSV parsing.
     *
     * FORMAT: sessionId|id|name|price|quantity|imageUrl
     *
     * @param sessionId  The unique ID of the user's browser session
     * @return           A single pipe-delimited string ready to write to file
     */
    public String toCsv(String sessionId) {
        return sessionId + "|" + id + "|" + name + "|" + price + "|" + quantity + "|" + imageUrl;
    }

    /**
     * Parses a single line from the text file back into a ProductItem object.
     * Skips the first field (sessionId) since the caller already checked it.
     *
     * FORMAT expected: sessionId|id|name|price|quantity|imageUrl
     *
     * @param csvLine  A full line read from wishlist_db.txt or cart_db.txt
     * @return         A ProductItem, or null if the line is malformed
     */
    public static ProductItem fromCsv(String csvLine) {
        // Limit to 6 parts so imageUrls with "|" inside won't get cut off
        String[] parts = csvLine.split("\\|", 6);
        if (parts.length >= 6) {
            // parts[0] = sessionId (already used for filtering, skip it)
            return new ProductItem(
                parts[1],                       // id
                parts[2],                       // name
                Double.parseDouble(parts[3]),   // price
                Integer.parseInt(parts[4]),     // quantity
                parts[5]                        // imageUrl
            );
        }
        return null; // Malformed line — ignore it
    }
}
