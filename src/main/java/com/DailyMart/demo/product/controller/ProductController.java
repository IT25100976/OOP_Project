package com.DailyMart.demo.product.controller;

import com.DailyMart.demo.product.model.FreshProduct;
import com.DailyMart.demo.product.model.PackagedProduct;
import com.DailyMart.demo.product.model.Product;
import com.DailyMart.demo.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Product Management.
 * Provides the API endpoints that the Frontend (HTML/JS) uses to communicate with the Backend.
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Allows cross-origin requests from pure HTML frontend pages
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    /**
     * API: Get all products.
     * Returns a JSON list of both Fresh and Packaged items.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.getAllProducts());
    }

    /**
     * API: Search products by keyword.
     * Filters by name or category (case-insensitive).
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        String kw = keyword != null ? keyword.toLowerCase() : "";
        List<Product> matches = productRepository.getAllProducts().stream()
                .filter(p -> {
                    String name = p.getName() != null ? p.getName().toLowerCase() : "";
                    String category = p.getCategory() != null ? p.getCategory().toLowerCase() : "";
                    return name.contains(kw) || category.contains(kw);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(matches);
    }

    /**
     * API: Add a Fresh Product (Vegetables/Fruits).
     */
    @PostMapping("/fresh")
    public ResponseEntity<String> addFreshProduct(@RequestBody FreshProduct product) {
        try {
            // Assign a simple ID based on current timestamp for uniqueness
            product.setId("F" + System.currentTimeMillis());
            product.setCategory("Fresh"); // Explicitly set for robustness
            productRepository.addProduct(product);
            return ResponseEntity.ok("Fresh Product added successfully!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * API: Add a Packaged Product (Grocery items).
     */
    @PostMapping("/packaged")
    public ResponseEntity<String> addPackagedProduct(@RequestBody PackagedProduct product) {
        try {
            product.setId("P" + System.currentTimeMillis());
            product.setCategory("Packaged"); // Explicitly set for robustness
            productRepository.addProduct(product);
            return ResponseEntity.ok("Packaged Product added successfully!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Internal Inner class for receiving partial product updates.
     */
    public static class UpdateRequest {
        public double price;
        public int stock;
    }

    /**
     * API: Update an existing product (Price or Stock).
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable String id, @RequestBody UpdateRequest request) {
        Product p = productRepository.getProductById(id);
        if (p != null) {
            p.setPrice(request.price);
            p.setStock(request.stock);
            productRepository.updateProduct(p);
            return ResponseEntity.ok("Product price and stock updated successfully!");
        }
        return ResponseEntity.status(404).body("Product not found");
    }

    /**
     * API: Delete a product by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        productRepository.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully!");
    }
}
