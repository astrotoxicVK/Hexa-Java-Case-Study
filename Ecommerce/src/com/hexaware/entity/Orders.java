package com.hexaware.entity;

import java.sql.Date;

public class Orders {
	private int order_id;
    private int customer_id;
    private Date order_date;
    private double total_price;
    private String shipping_address;

    // Default constructor
    public Orders() {
    }

    // Parameterized constructor
    public Orders(int order_id, int customer_id, Date order_date, double total_price, String shipping_address) {
        this.order_id = order_id;
        this.customer_id = customer_id;
        this.order_date = order_date;
        this.total_price = total_price;
        this.shipping_address = shipping_address;
    }

    // Getters and setters
    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address;
    }
}
