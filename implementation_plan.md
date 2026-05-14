# Implement Wishlist and Cart functionality

This plan outlines the steps to implement the backend and connect the frontend for `wishlist.html` and `cart.html` using a text file-based database, stored within a new `InventoryManagement` package.

## Proposed Changes

### 1. New Backend Package: `InventoryManagement`

We will create a new package `com.DailyMart.demo.InventoryManagement` to house the following classes:

- **`ProductItem.java` (Model)**: Represents an item in the wishlist or cart (ID, name, price, quantity, image URL).
- **`TextFileDatabaseHelper.java` (Utility)**: A helper class to read from and write to text files (`wishlist_db.txt`, `cart_db.txt`). It will handle serializing and deserializing `ProductItem` objects to/from text format (e.g., comma-separated values).
- **`WishlistService.java`**: Contains business logic for adding, removing, and retrieving items from the wishlist text file. Includes functionality to move an item from the wishlist to the cart.
- **`CartService.java`**: Contains business logic for adding, removing, updating quantity, and retrieving items from the cart text file.
- **`InventoryController.java` (REST Controller)**: Exposes endpoints for the frontend to interact with:
    - `GET /api/inventory/wishlist`
    - `POST /api/inventory/wishlist/add`
    - `DELETE /api/inventory/wishlist/remove/{id}`
    - `POST /api/inventory/wishlist/move-to-cart/{id}`
    - `GET /api/inventory/cart`
    - `POST /api/inventory/cart/add`
    - `DELETE /api/inventory/cart/remove/{id}`
    - `PUT /api/inventory/cart/update-quantity/{id}`

### 2. Frontend Connection

We will modify the static HTML files to fetch data from our new REST endpoints and dynamically render the content using JavaScript. 

#### [MODIFY] `wishlist.html` (`src/main/resources/static/wishlist.html`)
- Remove hardcoded wishlist items.
- Add JavaScript logic to fetch items from `/api/inventory/wishlist` on page load.
- Dynamically render the wishlist items.
- Implement click handlers for the "Delete" button (calls `DELETE` endpoint) and "Add to Cart" button (calls `move-to-cart` endpoint).
- Add HTML comments to clearly indicate where the backend connection happens.

#### [MODIFY] `cart.html` (`src/main/resources/static/cart.html`)
- Remove hardcoded cart items.
- Add JavaScript logic to fetch items from `/api/inventory/cart` on page load.
- Dynamically render the cart items and calculate the total/subtotal.
- Implement click handlers for quantity adjustments and deleting items.
- Ensure the checkout button properly links to `checkout.html` (which is currently statically linked).
- Add HTML comments to clearly indicate where the backend connection happens.

## Open Questions

> [!IMPORTANT]
> 1. Currently, there is no login/user-session context. Should the `wishlist_db.txt` and `cart_db.txt` act as a global database for the application, or would you like me to implement a simple session-based tracking (e.g., passing a generic session ID) so each user has their own list? *Assuming global lists for now based on simplicity of request.*
> 2. To test adding to the wishlist initially, would you like me to add a quick JS function to `shop.html` or `index.html` to add items, or is providing the backend endpoint and wiring up the wishlist/cart pages sufficient for this step?

## Verification Plan
1. Start the Spring Boot application.
2. Navigate to `wishlist.html`. Items should be loaded (initially empty).
3. Test adding an item (via a script or tool like Postman) and refresh to see it.
4. Test removing an item from the wishlist.
5. Test moving an item from the wishlist to the cart. Verify it disappears from the wishlist and appears in the cart.
6. Navigate to `cart.html` and verify the item is present.
7. Test updating quantity and removing items from the cart. Verify totals recalculate.
8. Verify that checkout link works.
