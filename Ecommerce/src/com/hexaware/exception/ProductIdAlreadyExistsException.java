package com.hexaware.exception;
/**
 * This is an exception
 */
public class ProductIdAlreadyExistsException extends Exception {
    public ProductIdAlreadyExistsException (String message) {
        super(message); 
    }
}