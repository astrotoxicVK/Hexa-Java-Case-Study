package com.hexaware.entity;

public class Cart {
	private int cart_id;
    private int customer_id;
    private int product_id;
    private int quantity;

    // Default constructor
    public Cart() {
    }

    // Parameterized constructor
    public Cart(int cart_id, int customer_id, int product_id, int quantity) {
        this.cart_id = cart_id;
        this.customer_id = customer_id;
        this.product_id = product_id;
        this.quantity = quantity;
    }

    // Getters and setters
    public int getCart_id() {
        return cart_id;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

