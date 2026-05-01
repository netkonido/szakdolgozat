package com.lazarbela.ikthesis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
public class IkthesisApplication {

	public static void main(String[] args) {
		SpringApplication.run(IkthesisApplication.class, args);
	}

	@Value("${corsOrigin.dev}")
	private String corsDevOrigin;

	@Value("${corsOrigin.start}")
	private String corsStartOrigin;

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")
						.allowedOrigins("http://localhost:5173/","http://localhost:3000/")
						.allowCredentials(true);
			}
		};
	}

}
