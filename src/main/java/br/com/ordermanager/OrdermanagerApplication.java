package br.com.ordermanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class OrdermanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdermanagerApplication.class, args);
	}
}


