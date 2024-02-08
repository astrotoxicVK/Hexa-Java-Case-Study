package com.hexaware.dao;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.hexaware.entity.Cart;
import com.hexaware.entity.Customer;
import com.hexaware.entity.Product;
import com.hexaware.exception.CustomerAlreadyExistsException;
import com.hexaware.exception.CustomerNotFoundException;
import com.hexaware.exception.OrderNotFoundException;
import com.hexaware.exception.ProductIdAlreadyExistsException;
import com.hexaware.exception.ProductNotFoundException;
public interface OrderProcessorRepository {
	boolean createProduct(Product product) throws ProductNotFoundException, ProductIdAlreadyExistsException;
    boolean createCustomer(Customer customer) throws CustomerAlreadyExistsException;
    boolean deleteCustomer(int customerId);
    boolean removeFromCart(Customer customer, Product product) throws SQLException, CustomerNotFoundException, ProductNotFoundException;
    List<Map<Product, Integer>> getOrdersByCustomer(int customerId);
	List<Product> viewCart(Customer customer);
	boolean placeOrder(int customerId, List<Map<Product, Integer>> productsAndQuantities, String shippingAddress) throws CustomerNotFoundException, ProductNotFoundException, SQLException, IllegalArgumentException;
	List<Map<Product, Integer>> viewCustomerOrders(int customerId) throws OrderNotFoundException, CustomerNotFoundException;
	boolean addToCart(int cart_id, int customerId, int productId, int quantity) throws CustomerNotFoundException;
	boolean deleteProduct(int productId) throws ProductNotFoundException;
	List<Product> viewAllProducts();


}



