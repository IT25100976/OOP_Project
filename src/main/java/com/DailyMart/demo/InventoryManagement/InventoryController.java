package com.DailyMart.demo.InventoryManagement;

// =============================================================================
// InventoryController.java
// PACKAGE: com.DailyMart.demo.InventoryManagement
//
// PURPOSE: This is the REST CONTROLLER — it is the "front door" of the backend.
//          It receives HTTP requests from the browser (HTML/JavaScript) and
//          delegates the actual work to InventoryService.
//
// HOW IT CONNECTS TO THE FRONTEND:
//   The HTML files connect to this controller via JavaScript fetch() calls.
//   The session ID is passed in every request as the "X-User-Id" HTTP header.
//   The header value is generated in:
//     → src/main/resources/static/assets/js/session.js
//
// ALL ENDPOINTS ARE PREFIXED WITH: /api/inventory
//
// WISHLIST ENDPOINTS:
//   GET    /api/inventory/wishlist                 ← wishlist-api.js: loadWishlist()
//   POST   /api/inventory/wishlist/add             ← shop.html: addToWishlist()
//   DELETE /api/inventory/wishlist/remove/{id}     ← wishlist-api.js: removeFromWishlist()
//   POST   /api/inventory/wishlist/move-to-cart/{id} ← wishlist-api.js: moveToCart()
//
// CART ENDPOINTS:
//   GET    /api/inventory/cart                     ← cart-api.js: loadCart()
//   POST   /api/inventory/cart/add                 ← shop.html: addToCart()
//   DELETE /api/inventory/cart/remove/{id}         ← cart-api.js: removeFromCart()
//   PUT    /api/inventory/cart/update-quantity/{id} ← cart-api.js: updateQuantity()
// =============================================================================

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController = @Controller + @ResponseBody
// Spring will automatically serialize the return values to JSON
@RestController
// All routes in this class are prefixed with /api/inventory
@RequestMapping("/api/inventory")
public class InventoryController {

    // Spring injects the InventoryService bean here automatically
    @Autowired
    private InventoryService inventoryService;

    // =========================================================================
    // WISHLIST ENDPOINTS
    // HTML file that connects here: wishlist.html
    // JS file that calls these: assets/js/wishlist-api.js
    // =========================================================================

    /**
     * GET /api/inventory/wishlist
     *
     * Returns the wishlist items for the current user's session as a JSON array.
     * → HTML: wishlist.html (the #wishlistContainer div is populated from this)
     * → JS:   wishlist-api.js → loadWishlist() → fetch('/api/inventory/wishlist')
     *
     * @param sessionId  Comes from the "X-User-Id" HTTP header set by session.js
     */
    @GetMapping("/wishlist")
    public ResponseEntity<List<ProductItem>> getWishlist(
            @RequestHeader("X-User-Id") String sessionId) {

        List<ProductItem> items = inventoryService.getWishlist(sessionId);
        return ResponseEntity.ok(items); // Returns 200 OK + JSON array
    }

    /**
     * POST /api/inventory/wishlist/add
     *
     * Adds a product to the wishlist. The product details are sent as JSON in the request body.
     * → HTML: shop.html (the heart icon / Wishlist button calls this)
     * → JS:   shop.html inline script → addToWishlist(id, name, price)
     *
     * @param sessionId  Comes from the "X-User-Id" HTTP header
     * @param item       The product data sent as JSON body from the frontend
     */
    @PostMapping("/wishlist/add")
    public ResponseEntity<String> addToWishlist(
            @RequestHeader("X-User-Id") String sessionId,
            @RequestBody ProductItem item) {

        inventoryService.addToWishlist(sessionId, item);
        return ResponseEntity.ok("Added to wishlist");
    }

    /**
     * DELETE /api/inventory/wishlist/remove/{id}
     *
     * Removes a product from the wishlist using its product ID.
     * → HTML: wishlist.html (the trash icon button calls this)
     * → JS:   wishlist-api.js → removeFromWishlist(id)
     *
     * @param sessionId  Comes from the "X-User-Id" HTTP header
     * @param id         The product ID to remove, taken from the URL path (e.g. /remove/P001)
     */
    @DeleteMapping("/wishlist/remove/{id}")
    public ResponseEntity<String> removeFromWishlist(
            @RequestHeader("X-User-Id") String sessionId,
            @PathVariable String id) {

        inventoryService.removeFromWishlist(sessionId, id);
        return ResponseEntity.ok("Removed from wishlist");
    }

    /**
     * POST /api/inventory/wishlist/move-to-cart/{id}
     *
     * Moves a product from the wishlist into the cart.
     * The product disappears from the wishlist and appears in the cart.
     * → HTML: wishlist.html (the cart icon button in each wishlist row)
     * → JS:   wishlist-api.js → moveToCart(id)
     *
     * @param sessionId  Comes from the "X-User-Id" HTTP header
     * @param id         The product ID to move (from the URL path)
     */
    @PostMapping("/wishlist/move-to-cart/{id}")
    public ResponseEntity<String> moveWishlistToCart(
            @RequestHeader("X-User-Id") String sessionId,
            @PathVariable String id) {

        inventoryService.moveWishlistToCart(sessionId, id);
        return ResponseEntity.ok("Moved to cart");
    }

    // =========================================================================
    // CART ENDPOINTS
    // HTML file that connects here: cart.html
    // JS file that calls these: assets/js/cart-api.js
    // =========================================================================

    /**
     * GET /api/inventory/cart
     *
     * Returns all cart items for the current session as a JSON array.
     * → HTML: cart.html (the #cartContainer tbody is populated from this)
     * → JS:   cart-api.js → loadCart() → fetch('/api/inventory/cart')
     *
     * @param sessionId  Comes from the "X-User-Id" HTTP header
     */
    @GetMapping("/cart")
    public ResponseEntity<List<ProductItem>> getCart(
            @RequestHeader("X-User-Id") String sessionId) {

        List<ProductItem> items = inventoryService.getCart(sessionId);
        return ResponseEntity.ok(items); // Returns 200 OK + JSON array
    }

    /**
     * POST /api/inventory/cart/add
     *
     * Adds a product to the cart. If it already exists, quantity is increased.
     * → HTML: shop.html ("Add to Cart" button on each product card)
     * → JS:   shop.html inline script → addToCart(id, name, price)
     *
     * @param sessionId  Comes from the "X-User-Id" HTTP header
     * @param item       The product data sent as JSON body from the frontend
     */
    @PostMapping("/cart/add")
    public ResponseEntity<String> addToCart(
            @RequestHeader("X-User-Id") String sessionId,
            @RequestBody ProductItem item) {

        inventoryService.addToCart(sessionId, item);
        return ResponseEntity.ok("Added to cart");
    }

    /**
     * DELETE /api/inventory/cart/remove/{id}
     *
     * Removes a product from the cart entirely.
     * → HTML: cart.html (the trash icon button in each cart row)
     * → JS:   cart-api.js → removeFromCart(id)
     *
     * @param sessionId  Comes from the "X-User-Id" HTTP header
     * @param id         The product ID to remove (from the URL path)
     */
    @DeleteMapping("/cart/remove/{id}")
    public ResponseEntity<String> removeFromCart(
            @RequestHeader("X-User-Id") String sessionId,
            @PathVariable String id) {

        inventoryService.removeFromCart(sessionId, id);
        return ResponseEntity.ok("Removed from cart");
    }

    /**
     * PUT /api/inventory/cart/update-quantity/{id}
     *
     * Updates the quantity of a cart item. If quantity = 0, item is removed.
     * → HTML: cart.html (the + / - quantity buttons and the number input field)
     * → JS:   cart-api.js → updateQuantity(id, quantity)
     *
     * @param sessionId  Comes from the "X-User-Id" HTTP header
     * @param id         The product ID to update (from the URL path)
     * @param quantity   The new quantity (from the ?quantity=N query parameter)
     */
    @PutMapping("/cart/update-quantity/{id}")
    public ResponseEntity<String> updateCartQuantity(
            @RequestHeader("X-User-Id") String sessionId,
            @PathVariable String id,
            @RequestParam int quantity) {

        inventoryService.updateCartQuantity(sessionId, id, quantity);
        return ResponseEntity.ok("Cart quantity updated");
    }

    /**
     * DELETE /api/inventory/cart/clear
     *
     * Empties the entire cart for the current session.
     * → JS: checkout.html → call this after successful /api/orders POST
     *
     * @param sessionId  Comes from the "X-User-Id" HTTP header
     */
    @DeleteMapping("/cart/clear")
    public ResponseEntity<String> clearCart(
            @RequestHeader("X-User-Id") String sessionId) {

        inventoryService.clearCart(sessionId);
        return ResponseEntity.ok("Cart cleared");
    }
}
