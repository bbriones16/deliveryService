package com.mynt.service.delivery.service.impl;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mynt.service.delivery.exception.ExternalServiceException;
import com.mynt.service.delivery.service.RestCommunicationService;

@Component
public class RestCommunicationServiceImpl implements RestCommunicationService {
	private static final String SOCKET_TIMEOUT_EXCEPTION = "SocketTimeoutException";
	private static final String SOCKET_EXCEPTION = "SocketException";
	private RestTemplate restTemplate;

	@Autowired
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public <T> T processRequest(String uri, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType)
			throws ExternalServiceException {
		ResponseEntity<T> restResponse = null;
		try {
			restResponse = restTemplate.exchange(uri, method, requestEntity, responseType);

		} catch (HttpStatusCodeException e) {
			String message = e.getMessage();
			int statusCode = e.getStatusCode().value();

			if (message.contains(SOCKET_EXCEPTION) || message.contains(SOCKET_TIMEOUT_EXCEPTION)) {
				statusCode = HttpStatus.REQUEST_TIMEOUT.value();
			}
			throw new ExternalServiceException(statusCode, message);
		} catch (ResourceAccessException e) {
			if (e.getRootCause() instanceof SocketTimeoutException) {
				throw new ExternalServiceException(HttpStatus.REQUEST_TIMEOUT.value(), e.getMessage());
			} else {
				throw new ExternalServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
			}
		} catch (Exception e) {
			throw new ExternalServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}

		return restResponse.getBody();
	}
			
	@SuppressWarnings("unused")
	private <T> void statusCodeCheck(ResponseEntity<T> restResponse) throws ExternalServiceException {
		HttpStatus statusCode = restResponse.getStatusCode();
		if (!HttpStatus.OK.equals(statusCode)) {
			throw new ExternalServiceException(restResponse.getStatusCode().value(), statusCode.getReasonPhrase());
		}
	}

	@SuppressWarnings("unused")
	private <T> void logResponseBody(ResponseEntity<T> restResponse) {
		String responseBody = toStringJSON(restResponse.getBody());
		System.out.println("RESPONSE_BODY: " + responseBody);
	}

	@SuppressWarnings("unused")
	private void logRequestBody(String url, HttpMethod method, HttpEntity<?> requestEntity) {
		System.out.println("EXT ENDPOINT: [" + method.toString() + "] " + url + "\tREQUEST_HEADERS: "
				+ requestEntity.getHeaders());
		String requestBody = toStringJSON(requestEntity.getBody());
		if (StringUtils.isNotEmpty(requestBody)) {
			System.out.println("REQUEST_BODY: " + requestBody);
		}
	}

	private String toStringJSON(Object entity) {
		if (entity != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.writeValueAsString(entity);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		return StringUtils.EMPTY;
	}			
}
