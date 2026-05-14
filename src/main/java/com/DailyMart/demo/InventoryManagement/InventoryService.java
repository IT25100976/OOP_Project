package com.DailyMart.demo.InventoryManagement;

// =============================================================================
// InventoryService.java
// PACKAGE: com.DailyMart.demo.InventoryManagement
//
// PURPOSE: This is the SERVICE layer — it contains all the BUSINESS LOGIC for
//          managing the wishlist and shopping cart. It talks directly to the
//          text file "database" (no MySQL used here).
//
// TEXT FILE DATABASE:
//   - wishlist_db.txt  → stores wishlist items (one line per item)
//   - cart_db.txt      → stores cart items (one line per item)
//
// SESSION-BASED TRACKING:
//   Each line starts with a sessionId (generated in the browser and sent as
//   the "X-User-Id" HTTP header). This way, each browser/user has their own
//   independent wishlist and cart stored in the same shared file.
//
// LINE FORMAT (pipe-separated):
//   sessionId|productId|productName|price|quantity|imageUrl
// =============================================================================

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// @Service tells Spring Boot to manage this class as a singleton bean
// so it can be injected with @Autowired in the controller
@Service
public class InventoryService {

    // File paths for our text-file "database"
    // These files are created in the project's working directory when the app starts
    private static final String WISHLIST_FILE = "wishlist_db.txt";
    private static final String CART_FILE     = "cart_db.txt";

    // Constructor: called once when Spring starts the application
    // Ensures the text files exist before any read/write happens
    public InventoryService() {
        createFileIfNotExists(WISHLIST_FILE);
        createFileIfNotExists(CART_FILE);
    }

    /**
     * Creates the text file if it doesn't already exist.
     * Called on startup for both wishlist_db.txt and cart_db.txt.
     */
    private void createFileIfNotExists(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("[InventoryService] Created new file: " + filename);
            } catch (IOException e) {
                System.err.println("[InventoryService] ERROR creating file: " + filename);
                e.printStackTrace();
            }
        }
    }

    // =========================================================================
    // COMMON FILE OPERATIONS (used internally by wishlist and cart methods)
    // =========================================================================

    /**
     * Reads all lines from a file that belong to the given sessionId.
     * Parses each line into a ProductItem and returns the list.
     *
     * @param filename   "wishlist_db.txt" or "cart_db.txt"
     * @param sessionId  The unique browser session ID (from "X-User-Id" header)
     * @return           List of ProductItem objects for this session
     */
    private List<ProductItem> getItems(String filename, String sessionId) {
        List<ProductItem> items = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip blank lines

                // Check the first field (sessionId) before full parse — fast filter
                String[] parts = line.split("\\|", 2);
                if (parts.length >= 2 && parts[0].equals(sessionId)) {
                    ProductItem item = ProductItem.fromCsv(line);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[InventoryService] ERROR reading file: " + filename);
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Reads ALL lines from a file (regardless of sessionId).
     * Used when we need to modify or delete specific lines.
     *
     * @param filename  The text file to read
     * @return          All non-blank lines as raw strings
     */
    private List<String> getAllLines(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("[InventoryService] ERROR reading file: " + filename);
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * Overwrites the entire file with the given list of lines.
     * Used after filtering out deleted/updated lines.
     * This is equivalent to "save all" in a simple database.
     *
     * @param filename  The text file to write
     * @param allLines  The complete list of lines to write (replaces old content)
     */
    private void saveAllItems(String filename, List<String> allLines) {
        // FileWriter with 'false' = overwrite mode (not append)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            for (String line : allLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[InventoryService] ERROR writing file: " + filename);
            e.printStackTrace();
        }
    }

    // =========================================================================
    // WISHLIST METHODS
    // Called by InventoryController when the frontend hits /api/inventory/wishlist/*
    // =========================================================================

    /**
     * Returns all wishlist items for the given session.
     * → Connected to: GET /api/inventory/wishlist  (in InventoryController.java)
     * → Called by:    wishlist-api.js → loadWishlist()
     */
    public List<ProductItem> getWishlist(String sessionId) {
        return getItems(WISHLIST_FILE, sessionId);
    }

    /**
     * Adds a product to the wishlist for the given session.
     * Prevents duplicate entries (same productId in the same session).
     * → Connected to: POST /api/inventory/wishlist/add  (in InventoryController.java)
     * → Called by:    wishlist-api.js (from shop.html → addToWishlist button)
     */
    public void addToWishlist(String sessionId, ProductItem item) {
        // Check for duplicate: if this product is already in the wishlist, do nothing
        List<ProductItem> existing = getWishlist(sessionId);
        boolean alreadyExists = existing.stream().anyMatch(i -> i.getId().equals(item.getId()));
        if (alreadyExists) {
            System.out.println("[InventoryService] Item already in wishlist: " + item.getId());
            return;
        }

        // Append the new item as a new line at the end of the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(WISHLIST_FILE, true))) {
            writer.write(item.toCsv(sessionId));
            writer.newLine();
            System.out.println("[InventoryService] Added to wishlist: " + item.getName());
        } catch (IOException e) {
            System.err.println("[InventoryService] ERROR adding to wishlist.");
            e.printStackTrace();
        }
    }

    /**
     * Removes a product from the wishlist for the given session.
     * Reads all lines, filters out the matching one, then rewrites the file.
     * → Connected to: DELETE /api/inventory/wishlist/remove/{id}  (in InventoryController.java)
     * → Called by:    wishlist-api.js → removeFromWishlist()  (trash icon in wishlist.html)
     */
    public void removeFromWishlist(String sessionId, String productId) {
        List<String> allLines = getAllLines(WISHLIST_FILE);

        // Keep every line EXCEPT the one matching this session + productId
        List<String> updatedLines = allLines.stream()
                .filter(line -> {
                    String[] parts = line.split("\\|", 3);
                    if (parts.length >= 3) {
                        // Remove this line only if both sessionId AND productId match
                        return !(parts[0].equals(sessionId) && parts[1].equals(productId));
                    }
                    return true; // Keep malformed lines to avoid data loss
                })
                .collect(Collectors.toList());

        saveAllItems(WISHLIST_FILE, updatedLines);
        System.out.println("[InventoryService] Removed from wishlist: " + productId);
    }

    // =========================================================================
    // CART METHODS
    // Called by InventoryController when the frontend hits /api/inventory/cart/*
    // =========================================================================

    /**
     * Returns all cart items for the given session.
     * → Connected to: GET /api/inventory/cart  (in InventoryController.java)
     * → Called by:    cart-api.js → loadCart()
     */
    public List<ProductItem> getCart(String sessionId) {
        return getItems(CART_FILE, sessionId);
    }

    /**
     * Adds a product to the cart for the given session.
     * If the product is already in the cart, increases quantity instead of adding a new line.
     * → Connected to: POST /api/inventory/cart/add  (in InventoryController.java)
     * → Called by:    cart-api.js / shop.html → addToCart button / wishlist "Move to Cart"
     */
    public void addToCart(String sessionId, ProductItem item) {
        List<String> lines = getAllLines(CART_FILE);
        boolean found = false;

        // Search for an existing line with the same sessionId + productId
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split("\\|", 6);
            if (parts.length >= 6 && parts[0].equals(sessionId) && parts[1].equals(item.getId())) {
                // Product already in cart: increase the quantity
                int currentQty = Integer.parseInt(parts[4]);
                parts[4] = String.valueOf(currentQty + item.getQuantity());
                lines.set(i, String.join("|", parts)); // Rebuild the line with new qty
                found = true;
                System.out.println("[InventoryService] Updated cart quantity for: " + item.getName());
                break;
            }
        }

        if (!found) {
            // Product not in cart yet: append it as a new line
            if (item.getQuantity() <= 0) item.setQuantity(1); // Default to 1 if not specified
            lines.add(item.toCsv(sessionId));
            System.out.println("[InventoryService] Added to cart: " + item.getName());
        }

        saveAllItems(CART_FILE, lines);
    }

    /**
     * Removes a product from the cart for the given session.
     * → Connected to: DELETE /api/inventory/cart/remove/{id}  (in InventoryController.java)
     * → Called by:    cart-api.js → removeFromCart()  (trash icon in cart.html)
     */
    public void removeFromCart(String sessionId, String productId) {
        List<String> allLines = getAllLines(CART_FILE);

        // Keep every line EXCEPT the one matching this session + productId
        List<String> updatedLines = allLines.stream()
                .filter(line -> {
                    String[] parts = line.split("\\|", 3);
                    if (parts.length >= 3) {
                        return !(parts[0].equals(sessionId) && parts[1].equals(productId));
                    }
                    return true;
                })
                .collect(Collectors.toList());

        saveAllItems(CART_FILE, updatedLines);
        System.out.println("[InventoryService] Removed from cart: " + productId);
    }

    /**
     * Updates the quantity of a specific cart item.
     * If quantity is set to 0 or less, the item is removed entirely.
     * → Connected to: PUT /api/inventory/cart/update-quantity/{id}  (in InventoryController.java)
     * → Called by:    cart-api.js → updateQuantity()  (+ / - buttons in cart.html)
     */
    public void updateCartQuantity(String sessionId, String productId, int quantity) {
        // If quantity drops to 0, just remove the item
        if (quantity <= 0) {
            removeFromCart(sessionId, productId);
            return;
        }

        List<String> lines = getAllLines(CART_FILE);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split("\\|", 6);
            if (parts.length >= 6 && parts[0].equals(sessionId) && parts[1].equals(productId)) {
                parts[4] = String.valueOf(quantity); // Update the quantity field
                lines.set(i, String.join("|", parts));
                System.out.println("[InventoryService] Updated quantity for " + productId + " to " + quantity);
                break;
            }
        }
        saveAllItems(CART_FILE, lines);
    }

    /**
     * Moves a product from the wishlist into the cart.
     * Finds the item in the wishlist, adds it to the cart, then removes it from wishlist.
     * → Connected to: POST /api/inventory/wishlist/move-to-cart/{id}  (in InventoryController.java)
     * → Called by:    wishlist-api.js → moveToCart()  (cart icon button in wishlist.html)
     */
    public void moveWishlistToCart(String sessionId, String productId) {
        // Step 1: Find the item in the wishlist
        List<ProductItem> wishlist = getWishlist(sessionId);
        ProductItem itemToMove = wishlist.stream()
                .filter(item -> item.getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (itemToMove != null) {
            itemToMove.setQuantity(1); // Start with quantity 1 when moving to cart

            // Step 2: Add it to the cart (will increase qty if already there)
            addToCart(sessionId, itemToMove);

            // Step 3: Remove it from the wishlist
            removeFromWishlist(sessionId, productId);

            System.out.println("[InventoryService] Moved to cart: " + itemToMove.getName());
        } else {
            System.out.println("[InventoryService] Item not found in wishlist: " + productId);
        }
    }
}
