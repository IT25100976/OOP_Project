/**
 * shared cart_management.js
 * Handles localStorage-based cart operations across DailyMart pages.
 */

const CART_KEY = 'dailymart_cart';

const CartManager = {
    // Get all items in cart
    getCart: function() {
        const cart = localStorage.getItem(CART_KEY);
        return cart ? JSON.parse(cart) : [];
    },

    // Add item to cart
    addItem: function(product) {
        let cart = this.getCart();
        const existing = cart.find(item => item.productId === product.id);
        if (existing) {
            existing.quantity += 1;
        } else {
            cart.push({
                productId: product.id,
                productName: product.name,
                unitPrice: product.price,
                quantity: 1
            });
        }
        localStorage.setItem(CART_KEY, JSON.stringify(cart));
        this.updateCartCount();
        alert(product.name + " added to cart!");
    },

    // Remove item
    removeItem: function(productId) {
        let cart = this.getCart();
        cart = cart.filter(item => item.productId !== productId);
        localStorage.setItem(CART_KEY, JSON.stringify(cart));
        this.updateCartCount();
    },

    // Update quantity
    updateQuantity: function(productId, qty) {
        let cart = this.getCart();
        const item = cart.find(i => i.productId === productId);
        if (item) {
            item.quantity = parseInt(qty);
            if (item.quantity <= 0) {
                this.removeItem(productId);
            } else {
                localStorage.setItem(CART_KEY, JSON.stringify(cart));
            }
        }
    },

    // Get total
    getTotal: function() {
        return this.getCart().reduce((sum, item) => sum + (item.unitPrice * item.quantity), 0);
    },

    // Clear cart
    clearCart: function() {
        localStorage.removeItem(CART_KEY);
        this.updateCartCount();
    },

    // Update UI Badge
    updateCartCount: function() {
        const cart = this.getCart();
        const count = cart.reduce((sum, item) => sum + item.quantity, 0);
        const badges = document.querySelectorAll('.bi-cart + .badge');
        badges.forEach(b => b.innerText = count);
    }
};

// Initialize count on load
document.addEventListener('DOMContentLoaded', () => CartManager.updateCartCount());
