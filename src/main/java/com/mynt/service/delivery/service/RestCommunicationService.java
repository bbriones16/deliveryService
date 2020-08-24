package com.mynt.service.delivery.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.mynt.service.delivery.exception.ExternalServiceException;

@Component
public interface RestCommunicationService {
	public <T> T processRequest(String uri, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType)
			throws ExternalServiceException;

}
