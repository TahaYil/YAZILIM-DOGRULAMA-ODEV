package com.taa.tshirtsatis.exception;

public class OrderNotFoundException extends RuntimeException {
	public OrderNotFoundException(String message) {
		super(message);
	}
}
