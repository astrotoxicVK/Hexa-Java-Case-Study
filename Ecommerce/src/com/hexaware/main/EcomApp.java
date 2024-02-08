/**
 *  This is Ecommerce Application
 *  
 *  @author Vaishnav
 *  @version1.0
 *  @since2024-02-04
 */

package com.hexaware.main;
import com.hexaware.dao.OrderProcessorRepositoryImpl;
import com.hexaware.entity.Customer;
import com.hexaware.entity.Product;
import com.hexaware.exception.CustomerAlreadyExistsException;
import com.hexaware.exception.CustomerNotFoundException;
import com.hexaware.exception.ProductIdAlreadyExistsException;
import com.hexaware.exception.ProductNotFoundException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * This is an Ecommerce Application where you will find customer and order details.
 */

  public class EcomApp {
 

    public static void main(String[] args) {
        System.out.println("******************----**********************WELCOME TO ECOMMERCE APPLICATION*****************---********************");
        System.out.println("*************************---*************************************************************---********************************************");
        System.out.println("********************************---****************************CASE STUDY***********---*******************************************");
        System.out.println("***************************************---**************************************---*****************************************************");
        OrderProcessorRepositoryImpl orderService = new OrderProcessorRepositoryImpl();
        Scanner sc = new Scanner(System.in);
        boolean showMenu = true;
        int choice;

        try {
            do {
                if (showMenu) {
                    System.out.println("Menu:");
                    System.out.println("1. Customer Management");
                    System.out.println("2. Product Management");
                    System.out.println("3. Cart Management");
                    System.out.println("4. Order Management");
                    System.out.println("0. Exit");
                    System.out.print("Enter your choice: ");
                    choice = sc.nextInt();
                } else {
                    choice = 0;
                }
                switch (choice) {
                    case 1: {
                        // Customer Management
                        customerManagement(orderService, sc);
                        break;
                    }
                    case 2: {
                        // Product Management
                        productManagement(orderService, sc);
                        break;
                    }
                    case 3: {
                        // Cart Management
                        cartManagement(orderService, sc);
                        break;
                    }
                    case 4: {
                        // Order Management
                        orderManagement(orderService, sc);
                        break;
                    }
                    case 0:
                        // Exit
                        System.out.println("Exiting the application!");
                        System.out.println("THANKYOU VISIT AGAIN!!!!!!!!!!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 0);
        } catch (Exception e) {
            System.out.println("An unexpected error occurred. Exiting the application.");
        } finally {
            sc.close();
        }
    }
 /**
  * This is customer management
  * @param orderService
  * @param sc
  */
    private static void customerManagement(OrderProcessorRepositoryImpl orderService, Scanner sc) {
        System.out.println("Customer Management:");
        System.out.println("1. Register Customer");
        System.out.println("2. Delete Customer");
        System.out.print("Enter your choice: ");
        int customerChoice = sc.nextInt();
        switch (customerChoice) {
            case 1:
                // Register Customer
                registerCustomer(orderService, sc);
                break;
            case 2:
                // Delete Customer
                deleteCustomer(orderService, sc);
                break;
            default:
                System.out.println("Invalid choice for Customer Management.");
        }
    }
/**
 * This is Customer Registration
 * @param orderService
 * @param sc
 */
    private static void registerCustomer(OrderProcessorRepositoryImpl orderService, Scanner sc) {
        try {
            System.out.print("Enter customer Id: ");
            int customerId = sc.nextInt();
            sc.nextLine(); // Consume the newline character
            if (orderService.isCustomerExists(customerId)) {
                throw new CustomerAlreadyExistsException("Customer with ID " + customerId + " already exists");
            }

            System.out.print("Enter customer name: ");
            String customerName = sc.nextLine();
            System.out.print("Enter customer email: ");
            String customerEmail = sc.nextLine();
            System.out.print("Enter customer password: ");
            String customerPassword = sc.nextLine();
            Customer customer = new Customer(customerId, customerName, customerEmail, customerPassword);
            orderService.createCustomer(customer);
            System.out.println("Customer registered successfully!");
        } catch (CustomerAlreadyExistsException e) {
            System.out.println(e.getMessage());
        }
    }
/**
 * This is customer deletion 
 * @param orderService
 * @param sc
 */
    private static void deleteCustomer(OrderProcessorRepositoryImpl orderService, Scanner sc) {
        System.out.print("Enter Customer ID to delete: ");
        int customerIdToDelete = sc.nextInt();
        boolean deleted = orderService.deleteCustomer(customerIdToDelete);
        if (deleted) {
            System.out.println("Customer deleted successfully!");
        } else {
            System.out.println("Customer not found. Deletion failed.");
        }
    }
    
    /**
     * This is Product Management
     * @param orderService
     * @param sc
     */
    private static void productManagement(OrderProcessorRepositoryImpl orderService, Scanner sc) {
        System.out.println("Product Management:");
        System.out.println("1. View Products");
        System.out.println("2. Add Product");
        System.out.println("3. Delete Product");
        System.out.print("Enter your choice: ");
        int productChoice = sc.nextInt();
        switch (productChoice) {
            case 1:
                // View Products
                viewProducts(orderService);
                break;
            case 2:
                // Add Product
                addProduct(orderService, sc);
                break;
            case 3:
                // Delete Product
                deleteProduct(orderService, sc);
                break;
            default:
                System.out.println("Invalid choice for Product Management.");
        }
    }

    /**
     * This is View Products
     * @param orderService
     */
    
    private static void viewProducts(OrderProcessorRepositoryImpl orderService) {
        List<Product> products = orderService.viewAllProducts();
        System.out.println("List of Products:");
        for (Product product : products) {
            System.out.println(product);
        }
    }
    
    /**
     * This is Add Product
     * @param orderService
     * @param sc
     */
    private static void addProduct(OrderProcessorRepositoryImpl orderService, Scanner sc) {
        System.out.println("Enter product details:");
        System.out.print("Enter product id: ");
        int productId = sc.nextInt();
        sc.nextLine(); // Consume the newline character
        if (orderService.isProductExists(productId)) {
            System.out.println("Product with ID " + productId + " already exists");
            return;
        }

        System.out.print("Enter product name: ");
        String productName = sc.nextLine();
        System.out.print("Enter product price: ");
        double productPrice = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter product description: ");
        String productDescription = sc.nextLine();
        System.out.print("Enter stock quantity: ");
        int stockQuantity = sc.nextInt();
        Product product = new Product(productId, productName, productPrice, productDescription, stockQuantity);

        try {
            boolean productAdded = orderService.createProduct(product);
            if (productAdded) {
                System.out.println("Product added successfully!");
            } else {
                System.out.println("Failed to add the product. Please try again.");
            }
        } catch (ProductIdAlreadyExistsException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This is Delete Product
     * @param orderService
     * @param sc
     */
    private static void deleteProduct(OrderProcessorRepositoryImpl orderService, Scanner sc) {
        try {
            System.out.print("Enter Product ID to delete: ");
            int productIdToDelete = sc.nextInt();
            boolean deleted = orderService.deleteProduct(productIdToDelete);
            if (deleted) {
                System.out.println("Product deleted successfully!");
            } else {
                System.out.println("Product not found. Deletion failed.");
            }
        } catch (ProductNotFoundException e) {
            System.out.println("Product not found: " + e.getMessage());
        } 
    }

/**
 * This is Cart Management
 * @param orderService
 * @param sc
 */
    private static void cartManagement(OrderProcessorRepositoryImpl orderService, Scanner sc) {
        System.out.println("Cart Management:");
        System.out.println("1. View Cart");
        System.out.println("2. Add to Cart");
        System.out.println("3. Remove from Cart");
        System.out.print("Enter your choice: ");
        int cartChoice = sc.nextInt();
        switch (cartChoice) {
            case 1:
                // View Cart
                viewCart(orderService, sc);
                break;
            case 2:
                // Add to Cart
                addToCart(orderService, sc);
                break;
            case 3:
                // Remove from Cart
                removeFromCart(orderService, sc);
                break;
            default:
                System.out.println("Invalid choice for Cart Management.");
        }
    }
/**
 * This is View Cart
 * @param orderService
 * @param sc
 */
    private static void viewCart(OrderProcessorRepositoryImpl orderService, Scanner sc) {
        System.out.print("Enter Customer ID: ");
        int customerIdToViewCart = sc.nextInt();
        Customer customerToViewCart = new Customer(customerIdToViewCart);
        List<Product> cartProducts = orderService.viewCart(customerToViewCart);
        System.out.println("Products in the cart:");
        for (Product cartProduct : cartProducts) {
            System.out.println(cartProduct);
        }
    }
/**
 * This is Add to Cart
 * @param orderService
 * @param sc
 */
    private static void addToCart(OrderProcessorRepositoryImpl orderService, Scanner sc) {
        System.out.print("Enter Cart ID: ");
        int cartIdToAddToCart = sc.nextInt();
        System.out.print("Enter Customer ID: ");
        int customerIdToAddToCart = sc.nextInt();
        System.out.print("Enter Product ID to add to cart: ");
        int productIdToAddToCart = sc.nextInt();
        System.out.print("Enter quantity: ");
        int quantityToAddToCart = sc.nextInt();
        try {
            orderService.addToCart(cartIdToAddToCart, customerIdToAddToCart, productIdToAddToCart, quantityToAddToCart);
            System.out.println("Product added to cart successfully!");
        } catch (CustomerNotFoundException e) {
            System.out.println("Customer not found. Add to Cart failed");
            e.printStackTrace();
        }
    }
    /** 
     *This is Remove from Cart
     * @param orderService
     * @param sc
     */
    private static void removeFromCart(OrderProcessorRepositoryImpl orderService, Scanner sc) {
        try {
            System.out.print("Enter Customer ID: ");
            int customerIdToRemoveFromCart = sc.nextInt();
            sc.nextLine(); 
            System.out.print("Enter Product ID to remove from cart: ");
            int productIdToRemoveFromCart = sc.nextInt();

            Customer customerToRemoveFromCart = new Customer(customerIdToRemoveFromCart);
            Product productToRemoveFromCart = new Product(productIdToRemoveFromCart);
            boolean removedFromCart = orderService.removeFromCart(customerToRemoveFromCart, productToRemoveFromCart);

            if (removedFromCart) {
                System.out.println("Product removed from cart successfully!");
            } else {
                System.out.println("Failed to remove product from cart. Please try again.");
            }
        } catch (ProductNotFoundException e) {
            System.out.println("Product not found: " + e.getMessage());
        } 
    }

/**
 * This is Order Management
 * @param orderService
 * @param sc
 * @throws SQLException 
 * @throws CustomerNotFoundException 
 * @throws ProductNotFoundException 
 * @throws IllegalArgumentException 
 */	
    private static void orderManagement(OrderProcessorRepositoryImpl orderService, Scanner sc) throws IllegalArgumentException, ProductNotFoundException, CustomerNotFoundException, SQLException {
        System.out.println("Order Management:");
        System.out.println("1. Place Order");
        System.out.println("2. View Customer Order");
        System.out.print("Enter your choice: ");
        int orderChoice = sc.nextInt();
        switch (orderChoice) {
            case 1:
                // Place Order
                placeOrder(orderService, sc);
                break;
            case 2:
                // View Customer Order
                viewCustomerOrder(orderService, sc);
                break;
            default:
                System.out.println("Invalid choice for Order Management.");
        }
    }
/**
 * This is Place Order
 * @param orderService
 * @param sc
 * @throws SQLException 
 * @throws  
 * @throws IllegalArgumentException 
 * @throws ProductNotFoundException 
 */
    private static void placeOrder(OrderProcessorRepositoryImpl orderService, Scanner sc) {
        try {
            System.out.print("Enter Customer ID: ");
            int customerIdToPlaceOrder = sc.nextInt();
            sc.nextLine(); // Consume the newline character
            System.out.print("Enter shipping address: ");
            String shippingAddress = sc.nextLine();
            // Initialize variables to store product ID and quantity
            List<Integer> productIds = new ArrayList<>();
            List<Integer> quantities = new ArrayList<>();
            // Loop to input products and quantities
            boolean continueAdding = true;
            while (continueAdding) {
                System.out.print("Enter Product ID: ");
                int productId = sc.nextInt();
                productIds.add(productId); // Store the product ID
                System.out.print("Enter Quantity: ");
                int quantity = sc.nextInt();
                quantities.add(quantity); // Store the quantity
                System.out.print("Do you want to add more products? (yes/no): ");
                String choice = sc.next();
                continueAdding = choice.equalsIgnoreCase("yes");
            }
            // Call the placeOrder method from OrderProcessorRepositoryImpl
            boolean orderPlaced = orderService.placeOrder(customerIdToPlaceOrder, productIds, quantities, shippingAddress);
            if (orderPlaced) {
                System.out.println("Order placed successfully!");
            } else {
                System.out.println("Failed to place the order. Please try again.");
            }
        } catch (CustomerNotFoundException e) {
            System.out.println("Customer not found: " + e.getMessage());
        } catch (ProductNotFoundException e) {
            System.out.println("Product not found: " + e.getMessage());
        }
    }

/**
 * This is View Customer Order
 * @param orderService
 * @param sc
 */private static void viewCustomerOrder(OrderProcessorRepositoryImpl orderService, Scanner sc) {
     System.out.print("Enter Customer ID: ");
     int customerIdToViewOrder = sc.nextInt();
     List<Map<Product, Integer>> customerOrders = orderService.getOrdersByCustomer(customerIdToViewOrder);
     System.out.println("Customer orders:");
     for (Map<Product, Integer> order : customerOrders) {
         for (Map.Entry<Product, Integer> entry : order.entrySet()) {
             System.out.println("Product: " + entry.getKey() + ", Quantity: " + entry.getValue());
         }
     }
 }
}