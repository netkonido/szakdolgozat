package com.lazarbela.ikthesis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@EnableScheduling
public class IkthesisApplication {

	public static void main(String[] args) {
		SpringApplication.run(IkthesisApplication.class, args);
	}

}
