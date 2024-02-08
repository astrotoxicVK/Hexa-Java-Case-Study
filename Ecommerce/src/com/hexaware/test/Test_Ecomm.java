package com.hexaware.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.hexaware.dao.OrderProcessorRepositoryImpl;
import com.hexaware.entity.Customer;
import com.hexaware.entity.Product;
import com.hexaware.exception.CustomerNotFoundException;
import com.hexaware.exception.ProductIdAlreadyExistsException;
import com.hexaware.exception.ProductNotFoundException;

/**
 * Test class for testing the functionalities of the OrderProcessorRepositoryImpl class.
 */
public class Test_Ecomm {

    /**
     * Test method for creating a product.
     *
     * @throws ProductIdAlreadyExistsException when the product id already exists.
     */
    @Test
    public void testCreateProduct_Success() throws ProductIdAlreadyExistsException {
        // Arrange
        Product product = new Product(66, "Test Product", 10.0, "Test Description", 100);
        OrderProcessorRepositoryImpl repository = new OrderProcessorRepositoryImpl();

        // Act
        boolean result = repository.createProduct(product);

        // Assert
        assertTrue(result);
    }

    /**
     * Test method for adding a product to the cart.
     *
     * @throws CustomerNotFoundException when the customer is not found.
     */
    @Test
    public void testAddToCart_Success() throws CustomerNotFoundException {
        // Arrange
        Customer customer = new Customer(6, "Test Customer", "test@example.com", "password");
        Product product = new Product(66, "Test Product", 10.0, "Test Description", 100);
        OrderProcessorRepositoryImpl repository = new OrderProcessorRepositoryImpl();

        // Act
        boolean result = repository.addToCart(1, customer.getCustomer_id(), product.getProduct_id(), 1);

        // Assert
        assertTrue(result);
    }

    /**
     * Test method for placing an order.
     * @throws SQLException 
     * @throws IllegalArgumentException 
     */
    @Test
    public void testPlaceOrder_Success() throws IllegalArgumentException, SQLException {
        // Arrange
        Customer customer = new Customer(1, "Test Customer", "test@example.com", "password");
        Product product1 = new Product(11, "Product 1", 10.0, "Description 1", 100);
        Product product2 = new Product(22, "Product 2", 15.0, "Description 2", 150);
        List<Map<Product, Integer>> productsAndQuantities = new ArrayList<>();
        Map<Product, Integer> product1Map = new HashMap<>();
        Map<Product, Integer> product2Map = new HashMap<>();
        product1Map.put(product1, 2); // 2 units of Product 1
        product2Map.put(product2, 1); // 1 unit of Product 2
        productsAndQuantities.add(product1Map);
        productsAndQuantities.add(product2Map);
        String shippingAddress = "140 Test Street, Test Town";
        OrderProcessorRepositoryImpl repository = new OrderProcessorRepositoryImpl();

        // Act
        boolean result = false;
        try {
            // Since it's a new order, orderId is not needed
            result = repository.placeOrder(customer.getCustomer_id(), productsAndQuantities, shippingAddress);
        } catch (CustomerNotFoundException | ProductNotFoundException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }

        // Assert
        assertTrue(result);
    }


    /**
     * Test method for checking if CustomerNotFoundException is thrown when customer is not found.
     *
     * @throws CustomerNotFoundException when the customer is not found.
     */
    @Test(expected = CustomerNotFoundException.class)
    public void testException_CustomerNotFound() throws CustomerNotFoundException {
        // Arrange
        OrderProcessorRepositoryImpl repository = new OrderProcessorRepositoryImpl();

        // Act & Assert
        repository.viewCustomerOrders(1000);
    }

    /**
     * Test method for checking if ProductNotFoundException is thrown when product is not found.
     *
     * @throws ProductNotFoundException when the product is not found.
     */
    @Test(expected = ProductNotFoundException.class)
    public void testException_ProductNotFound() throws ProductNotFoundException {
        // Arrange
        OrderProcessorRepositoryImpl repository = new OrderProcessorRepositoryImpl();

        // Act & Assert
        repository.getProductById(1000);
    }
}