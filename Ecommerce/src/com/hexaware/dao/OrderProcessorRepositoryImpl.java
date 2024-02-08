package com.hexaware.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hexaware.entity.Customer;
import com.hexaware.entity.Product;
import com.hexaware.exception.CustomerAlreadyExistsException;
import com.hexaware.exception.CustomerNotFoundException;
import com.hexaware.exception.ProductIdAlreadyExistsException;
import com.hexaware.exception.ProductNotFoundException;
import com.hexware.util.DBConnUtil;
/**
 * This is OrderProcessorRepositoryImplOrder Class
 */
public class OrderProcessorRepositoryImpl implements OrderProcessorRepository {

    @Override
    public boolean createProduct(Product product) throws ProductIdAlreadyExistsException {
        if (productExists(product.getProduct_id())) {
            throw new ProductIdAlreadyExistsException("Product with ID " + product.getProduct_id() + " already exists.");
        }

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO products (product_id, name, price, description, stockQuantity) VALUES (?, ?,?,?,?)")) {
            statement.setInt(1, product.getProduct_id());
            statement.setString(2, product.getName());
            statement.setDouble(3, product.getPrice());
            statement.setString(4, product.getDescription());
            statement.setInt(5, product.getStockQuantity());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createCustomer(Customer customer) throws CustomerAlreadyExistsException {
        if (customerExists(customer.getCustomer_id())) {
            throw new CustomerAlreadyExistsException("Customer with ID " + customer.getCustomer_id() + " already exists.");
        }

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO customers (customer_id, name, email, password) VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, customer.getCustomer_id());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getEmail());
            statement.setString(4, customer.getPassword());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean customerExists(int customerid) {
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM customers WHERE customer_id = ?")) {
            statement.setInt(1, customerid);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
   
    public List<Map<Product, Integer>> viewCustomerOrders(int customerId) throws CustomerNotFoundException {
        List<Map<Product, Integer>> customerOrders = new ArrayList<>();
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT orders.order_id, orders.order_date, orders.total_price, orders.shipping_address, " +
                             "Order_Items.product_id, Order_Items.quantity, products.name, products.price, products.description, products.stockQuantity " +
                             "FROM orders " +
                             "JOIN Order_Items ON orders.order_id = Order_Items.order_id " +
                             "JOIN products ON Order_Items.product_id = products.product_id " +
                             "WHERE orders.customer_id = ?")) {
            statement.setInt(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new CustomerNotFoundException("Customer with ID " + customerId + " not found.");
                }

                resultSet.beforeFirst(); // Reset cursor position

                while (resultSet.next()) {
                    int orderId = resultSet.getInt("order_id");
                    String orderDate = resultSet.getString("order_date");
                    int totalPrice = resultSet.getInt("total_price");
                    String shippingAddress = resultSet.getString("shipping_address");
                    int productId = resultSet.getInt("product_id");
                    int quantity = resultSet.getInt("quantity");
                    String productName = resultSet.getString("name");
                    double productPrice = resultSet.getDouble("price");
                    String productDescription = resultSet.getString("description");
                    int stockQuantity = resultSet.getInt("stockQuantity");

                    Product product = new Product(productId, productName, productPrice, productDescription, stockQuantity);
                    Map<Product, Integer> orderDetails = new HashMap<>();
                    orderDetails.put(product, quantity);

                    customerOrders.add(orderDetails);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerOrders;
    }

    
    public boolean placeOrder(int customerId, List<Integer> productIds, List<Integer> quantities, String shippingAddress)
            throws CustomerNotFoundException, ProductNotFoundException {
        if (!customerExists(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found.");
        }

        try (Connection connection = DBConnUtil.getConnection()) {
            try (PreparedStatement orderStatement = connection.prepareStatement(
                    "INSERT INTO orders (customer_id, order_date, total_price, shipping_address) VALUES (?, CURRENT_DATE, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                orderStatement.setInt(1, customerId);
                int totalPrice = calculateTotalPrice(productIds, quantities);
                orderStatement.setInt(2, totalPrice);
                orderStatement.setString(3, shippingAddress);

                int rowsAffected = orderStatement.executeUpdate();

                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = orderStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int orderId = generatedKeys.getInt(1);
                            try (PreparedStatement orderItemsStatement = connection.prepareStatement(
                                    "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)")) {
                                // Iterate over productIds and quantities to insert order items
                                for (int i = 0; i < productIds.size(); i++) {
                                    orderItemsStatement.setInt(1, orderId);
                                    orderItemsStatement.setInt(2, productIds.get(i));
                                    orderItemsStatement.setInt(3, quantities.get(i));
                                    orderItemsStatement.addBatch(); // Add batch for efficient execution
                                }
                                orderItemsStatement.executeBatch(); // Execute the batch
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private int calculateTotalPrice(List<Integer> productIds, List<Integer> quantities) throws SQLException, ProductNotFoundException {
        int totalPrice = 0;
        for (int i = 0; i < productIds.size(); i++) {
            int productId = productIds.get(i);
            int quantity = quantities.get(i);
            // Assuming you have a method to retrieve the price of the product by its ID
            int price = getProductById(productId);
            totalPrice += price * quantity;
        }
        return totalPrice;
    }



    @Override
    public List<Product> viewCart(Customer customer) {
        List<Product> cartProducts = new ArrayList<>();
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT products.* FROM cart " +
                             "JOIN products ON cart.product_id = products.product_id " +
                             "WHERE cart.customer_id = ?")) {
            statement.setInt(1, customer.getCustomer_id());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int productId = resultSet.getInt("product_id");
                    String productName = resultSet.getString("name");
                    double productPrice = resultSet.getDouble("price");
                    String productDescription = resultSet.getString("description");
                    int stockQuantity = resultSet.getInt("stockQuantity");

                    Product product = new Product(productId, productName, productPrice, productDescription, stockQuantity);
                    cartProducts.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartProducts;
    }
    private boolean productExists(int productId) {
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM products WHERE product_id = ?")) {
            statement.setInt(1, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	@Override
	public boolean deleteCustomer(int customerId) {
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM customers WHERE customer_id = ?")) {
            statement.setInt(1, customerId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Catching SQLIntegrityConstraintViolationException specifically
            if (e instanceof SQLIntegrityConstraintViolationException) {
                System.out.println("Cannot delete the customer because they have associated records in other tables.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }
	@Override
	public boolean removeFromCart(Customer customer, Product product) throws  ProductNotFoundException {
	    try (Connection connection = DBConnUtil.getConnection();
	         PreparedStatement statement = connection.prepareStatement(
	                 "DELETE FROM cart WHERE customer_id = ? AND product_id = ?")) {
	        statement.setInt(1, customer.getCustomer_id());
	        statement.setInt(2, product.getProduct_id());
	        int rowsAffected = statement.executeUpdate();
	        if (rowsAffected > 0) {
	            return true;
	        } else {
	            throw new ProductNotFoundException("Product not found in the cart.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; 
	    }
	}


	@Override
	public List<Map<Product, Integer>> getOrdersByCustomer(int customerId) {
	    List<Map<Product, Integer>> customerOrders = new ArrayList<>();
	    try (Connection connection = DBConnUtil.getConnection();
	         PreparedStatement statement = connection.prepareStatement(
	                 "SELECT orders.order_id, orders.order_date, orders.shipping_address, " +
	                         "Order_Items.product_id, SUM(Order_Items.quantity) AS total_quantity, " +
	                         "products.name, products.price, products.description, products.stockQuantity " +
	                         "FROM orders " +
	                         "JOIN Order_Items ON orders.order_id = Order_Items.order_id " +
	                         "JOIN products ON Order_Items.product_id = products.product_id " +
	                         "WHERE orders.customer_id = ? " +
	                         "GROUP BY orders.order_id, Order_Items.product_id")) {
	        statement.setInt(1, customerId);

	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	                int orderId = resultSet.getInt("order_id");
	                String orderDate = resultSet.getString("order_date");
	                String shippingAddress = resultSet.getString("shipping_address");
	                int productId = resultSet.getInt("product_id");
	                int totalQuantity = resultSet.getInt("total_quantity");
	                String productName = resultSet.getString("name");
	                double productPrice = resultSet.getDouble("price");
	                String productDescription = resultSet.getString("description");
	                int stockQuantity = resultSet.getInt("stockQuantity");

	                Product product = new Product(productId, productName, productPrice, productDescription, stockQuantity);
	                Map<Product, Integer> orderDetails = new HashMap<>();
	                orderDetails.put(product, totalQuantity);

	                customerOrders.add(orderDetails);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return customerOrders;
	}
	@Override
    public boolean deleteProduct(int productId) throws ProductNotFoundException {

        if (!productExists(productId)) {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }

        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM products WHERE product_id = ?")) {
            statement.setInt(1, productId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	public boolean isCustomerExists(int customerId) {
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM customers WHERE customer_id = ?")) {
            statement.setInt(1, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	public boolean isProductExists(int productId) {
        try (Connection connection = DBConnUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM products WHERE product_id = ?")) {
            statement.setInt(1, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	 @Override
	    public boolean addToCart(int cartId, int customerId, int productId, int quantity) throws CustomerNotFoundException {
	        if (!customerExists(customerId)) {
	            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found.");
	        }

	        try (Connection connection = DBConnUtil.getConnection();
	             PreparedStatement statement = connection.prepareStatement(
	                     "INSERT INTO cart (cart_id, customer_id, product_id, Quantity) VALUES (?, ?, ?,?)")) {
	            statement.setInt(1, cartId);
	            statement.setInt(2, customerId);
	            statement.setInt(3, productId);
	            statement.setInt(4, quantity);
	            int rowsAffected = statement.executeUpdate();
	            return rowsAffected > 0;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	 @Override
	 public List<Product> viewAllProducts() {
	     List<Product> products = new ArrayList<>();
	     try (Connection connection = DBConnUtil.getConnection();
	          PreparedStatement statement = connection.prepareStatement("SELECT * FROM products")) {
	         try (ResultSet resultSet = statement.executeQuery()) {
	             while (resultSet.next()) {
	                 int productId = resultSet.getInt("product_id");
	                 String productName = resultSet.getString("name");
	                 double productPrice = resultSet.getDouble("price");
	                 String productDescription = resultSet.getString("description");
	                 int stockQuantity = resultSet.getInt("stockQuantity");

	                 Product product = new Product(productId, productName, productPrice, productDescription, stockQuantity);
	                 products.add(product);
	             }
	         }
	     } catch (SQLException e) {
	         e.printStackTrace();
	     }
	     return products;
	 }
	
	 public int getProductById(int productId) throws ProductNotFoundException {
		    try (Connection connection = DBConnUtil.getConnection()) {
		        PreparedStatement statement = connection.prepareStatement("SELECT price FROM products WHERE product_id = ?");
		        statement.setInt(1, productId);
		        ResultSet resultSet = statement.executeQuery();
		        if (resultSet.next()) {
		            return resultSet.getInt("price");
		        } else {
		            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		        throw new ProductNotFoundException("Error occurred while retrieving product with ID " + productId);
		    }
		}

	@Override
	public boolean placeOrder(int customerId, List<Map<Product, Integer>> productsAndQuantities, String shippingAddress)
			throws CustomerNotFoundException, ProductNotFoundException, SQLException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}
}

