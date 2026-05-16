package com.DailyMart.demo.order.controller;

import com.DailyMart.demo.order.model.Order;
import com.DailyMart.demo.order.model.OrderItem;
import com.DailyMart.demo.order.repository.OrderRepository;
import com.DailyMart.demo.product.model.Product;
import com.DailyMart.demo.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * REST Controller for Order Management.
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * API: Get all orders.
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.getAllOrders());
    }

    /**
     * API: Place a new order.
     * Decrements stock for each item.
     */
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Order order) {
        try {
            // 1. Generate ID and Date
            order.setOrderId("ORD" + System.currentTimeMillis());
            order.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            order.setStatus("Pending");

            // 2. Process Items, Fetch Names, and Deduct Stock
            for (OrderItem item : order.getItems()) {
                Product p = productRepository.getProductById(item.getProductId());
                if (p != null) {
                    // Always set/update product name from the definitive repository
                    item.setProductName(p.getName());
                    
                    if (p.getStock() < item.getQuantity()) {
                        return ResponseEntity.badRequest().body("Insufficient stock for: " + p.getName());
                    }
                    p.setStock(p.getStock() - item.getQuantity());
                    productRepository.updateProduct(p);
                } else if (item.getProductName() == null || item.getProductName().isEmpty()) {
                    // Fallback if product not found in repo (shouldn't happen with valid IDs)
                    item.setProductName("Product");
                }
            }

            // 3. Save Order
            orderRepository.addOrder(order);

            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * API: Update Order Status.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable String id, @RequestParam String status) {
        orderRepository.updateOrderStatus(id, status);
        return ResponseEntity.ok("Order status updated to: " + status);
    }
}
