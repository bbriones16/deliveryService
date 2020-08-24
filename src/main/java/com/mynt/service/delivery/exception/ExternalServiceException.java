package com.mynt.service.delivery.exception;

public class ExternalServiceException extends Exception{

	private final int statusCode;
	private final String message;

	private static final long serialVersionUID = 1L;

	public ExternalServiceException(int statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
		this.message = message;
	}

	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
