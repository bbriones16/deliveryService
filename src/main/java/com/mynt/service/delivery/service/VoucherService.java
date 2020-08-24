package com.mynt.service.delivery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.mynt.service.delivery.dom.Voucher;
import com.mynt.service.delivery.exception.ExternalServiceException;
import com.mynt.service.delivery.util.APIConstants;

@Component
public class VoucherService {
	private RestCommunicationService restService;

	private static final String HTTP = "http";

	@Value("${voucher.service.host}")
	private String host;

	@Value("${voucher.service.api.key}")
	private String key;

	@Autowired
	private void setRestService(RestCommunicationService restService) {
		this.restService = restService;
	}

	public Voucher getVoucher(String voucherCode) throws ExternalServiceException{
		String uri = UriComponentsBuilder.newInstance().scheme(HTTP).host(host).path(APIConstants.VOUCHER_SERVICE_URI)
				.path("/" + voucherCode).queryParam(APIConstants.API_KEY, key).build().toUriString();
		
		System.out.println("URI = " + uri);
		
		Voucher voucher = restService.processRequest(uri, HttpMethod.GET, createHttpEntity(), Voucher.class);
		
		return voucher;
	}

	private HttpEntity<String> createHttpEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<>(headers);
	}

}
