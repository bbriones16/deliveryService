package com.uhg.genesys.shared.ibmmq.client;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.uhg.genesys.shared.ibmmq.exceptions.BenefitAppServiceException;
import com.uhg.genesys.shared.ibmmq.responseobject.ResponseBenefitAppDetails;

public class RestClient<T, K> {

	private Class<T> typeParameterClass;
	UriComponentsBuilder builder;
	HttpHeaders headers = new HttpHeaders();
	ResponseEntity<T> result;
	private String url = "";
	private int timeout;
	private static final Logger log = LoggerFactory.getLogger(RestClient.class);

	private static final String SOCKET_TIMEOUT_EXCEPTION = "SocketTimeoutException";
	private static final String SOCKET_EXCEPTION = "SocketException";

	public RestClient(Class<T> clazz) {
		typeParameterClass = clazz;
	}
	
	public ResponseBenefitAppDetails<T> postResponse(K request,String httpMethodType) throws BenefitAppServiceException{
		log.info("RestClient.getResponse entering method");

		SSLConnectionSocketFactory csf = getConnectionSocketFactory();
		CloseableHttpClient httpclient = null;
		ResponseBenefitAppDetails<T> response = null;
		ResponseEntity<T> result = null;

		try {
			httpclient = HttpClients.custom().setSSLSocketFactory(csf).build();

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpclient);
			requestFactory.setReadTimeout(timeout);
			requestFactory.setConnectTimeout(timeout);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Object> entity = new HttpEntity<Object>(request, headers);
			
			response = new ResponseBenefitAppDetails<T>();
			result = restTemplate.exchange(url, HttpMethod.POST, entity, typeParameterClass);

			response.setData((T) result.getBody());
			response.setStatusCode(result.getStatusCode().value());
			if (result.getStatusCode() != null)
				response.setStatusMessage(result.getStatusCode().getReasonPhrase());
		}catch (HttpStatusCodeException e) {
			String message = e.getMessage();
			int statusCode = e.getStatusCode().value();

			if (message.contains(SOCKET_EXCEPTION) || message.contains(SOCKET_TIMEOUT_EXCEPTION)) {
				statusCode = HttpStatus.REQUEST_TIMEOUT.value();
			}
			log.error("ERROR MESSAGE : " + message);
			log.info("STATUS + " + statusCode);
			throw new BenefitAppServiceException("" + statusCode, message);
		} catch (ResourceAccessException e) {
			if (e.getRootCause() instanceof SocketTimeoutException) {
				throw new BenefitAppServiceException("" + HttpStatus.REQUEST_TIMEOUT.value(), e.getMessage());
			} else {
				throw new BenefitAppServiceException("" + HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
			}
		} catch (Exception e) {
			log.error("Exception Occurred in Rest Client " + e.getMessage());
			throw new BenefitAppServiceException("" + HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		} finally {
			if (httpclient != null)
				try {
					httpclient.close();
					} catch (IOException e) {
						log.warn("Error closing the file");
						//log.error("Error Message = " + e.getMessage());
					}
		}
		return response;
	}

	private SSLConnectionSocketFactory getConnectionSocketFactory() {
		log.info("RestClient.getConnectionSocketFactory entering method");
		TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
			@Override
			public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
				return true;
			}
		};
		SSLContext sslContext = null;
		try {
			sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e1) {
			log.error("Error");
		}

		// Dummy value necessary to call right SSLConnectionSocketFactory
		// constructor
		HostnameVerifier hostnameVerifier = null;

		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1.2", "TLSv1.1", "TLSv1" }, null, hostnameVerifier);
		return csf;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	
	public ResponseBenefitAppDetails<T> getResponse(K request,String httpMethodType) {
		log.info("RestClient.getResponse entering method");

		SSLConnectionSocketFactory csf = getConnectionSocketFactory();
		CloseableHttpClient httpclient = null;
		ResponseBenefitAppDetails<T> response = null;
		ResponseEntity<T> result = null;

		try {
			httpclient = HttpClients.custom().setSSLSocketFactory(csf).build();

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

			requestFactory.setHttpClient(httpclient);
			requestFactory.setReadTimeout(timeout);
			requestFactory.setConnectTimeout(timeout);

			RestTemplate restTemplate = new RestTemplate(requestFactory);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Object> entity = new HttpEntity<Object>(request, headers);
			
			response = new ResponseBenefitAppDetails<T>();
			result = restTemplate.exchange(url, HttpMethod.POST, entity, typeParameterClass);
			
			response.setData((T) result.getBody());
			response.setStatusCode(result.getStatusCode().value());
			if (result.getStatusCode() != null)
				response.setStatusMessage(result.getStatusCode().getReasonPhrase());
		} catch (Exception e) {
			log.error("Exception Occurred in Rest Client " + e.getMessage());
			response.setStatusMessage(e.getMessage());
		} finally {
			if (httpclient != null)
				try {
					httpclient.close();
					} catch (IOException e) {
						log.warn("Error closing the file");
						//log.error("Error Message = " + e.getMessage());
					}
		}
		return response;
	}
}
