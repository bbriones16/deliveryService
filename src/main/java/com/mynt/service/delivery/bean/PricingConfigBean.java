package com.mynt.service.delivery.bean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mynt.service.delivery.resource.pricing.Pricing;
import com.mynt.service.delivery.resource.pricing.PricingRepository;

@Component
public class PricingConfigBean {
	@Autowired
	private PricingRepository pricingRepository;
	
	private List<Pricing> pricingConfig;
	
	@PostConstruct
	public void init() {
		Iterable<Pricing> pricingIterable = pricingRepository.findAll();
		pricingConfig = StreamSupport.stream(pricingIterable.spliterator(),false).collect(Collectors.toList());
	}
	
	public List<Pricing> getPricingConfig(){
		return pricingConfig;
	}
}
