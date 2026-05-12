package com.DailyMart.demo.product.repository;

import com.DailyMart.demo.product.model.FreshProduct;
import com.DailyMart.demo.product.model.PackagedProduct;
import com.DailyMart.demo.product.model.Product;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Layer for Products.
 * Uses a flat CSV file (products.txt) to simulate a database.
 */
@Repository
public class ProductRepository {

    private static final String FILE_PATH = "products.txt";

    public ProductRepository() {
        // Initialization: Create the text file if it doesn't exist yet
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
     * READ: Loads all products from the text file.
     * Parses each CSV line back into either FreshProduct or PackagedProduct objects.
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 6) {
                    String id = data[0];
                    String name = data[1];
                    double price = Double.parseDouble(data[2]);
                    int stock = Integer.parseInt(data[3]);
                    String category = data[4];
                    String specific = data[5];

                    // Logic to recreate the correct object type based on ID prefix
                    if (id.startsWith("F")) {
                        products.add(new FreshProduct(id, name, category, price, stock, Double.parseDouble(specific)));
                    } else if (id.startsWith("P")) {
                        products.add(new PackagedProduct(id, name, category, price, stock, specific));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Helper: Find a specific product by its Unique ID
    public Product getProductById(String id) {
        return getAllProducts().stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * CREATE: Adds a new product by appending its string representation to the file.
     */
    public void addProduct(Product product) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(product.toFileString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * UPDATE: Re-writes the entire file, replacing the modified product's line.
     */
    public void updateProduct(Product updatedProduct) {
        List<Product> products = getAllProducts();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Product p : products) {
                if (p.getId().equals(updatedProduct.getId())) {
                    bw.write(updatedProduct.toFileString());
                } else {
                    bw.write(p.toFileString());
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * DELETE: Re-writes the entire file excluding the line matching the provided ID.
     */
    public void deleteProduct(String id) {
        List<Product> products = getAllProducts();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Product p : products) {
                if (!p.getId().equals(id)) {
                    bw.write(p.toFileString());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
