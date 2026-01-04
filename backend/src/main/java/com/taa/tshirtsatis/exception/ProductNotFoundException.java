package com.taa.tshirtsatis.exception;

public class ProductNotFoundException extends RuntimeException{
	public ProductNotFoundException(String message) {
        super(message);
    }
}
