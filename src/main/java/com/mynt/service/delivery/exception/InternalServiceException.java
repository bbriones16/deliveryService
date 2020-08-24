package com.mynt.service.delivery.exception;

public class InternalServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2305997644664923871L;
	private final String status;
	private final String message;
	public InternalServiceException(String status, String message) {
		super(message);
		this.status = status;
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
