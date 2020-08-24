package com.mynt.service.delivery;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@EnableJpaRepositories(basePackages = "com.mynt.service.delivery.resource.*")
@Configuration
@ComponentScan(basePackages = { "com.mynt.service.*" })
public class Config {

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

}
