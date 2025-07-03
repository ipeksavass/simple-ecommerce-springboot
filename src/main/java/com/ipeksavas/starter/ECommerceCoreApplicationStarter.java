package com.ipeksavas.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.ipeksavas"})
@ComponentScan(basePackages = {"com.ipeksavas"})
@EnableJpaRepositories(basePackages = {"com.ipeksavas"})
public class ECommerceCoreApplicationStarter {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceCoreApplicationStarter.class, args);
	}

}
