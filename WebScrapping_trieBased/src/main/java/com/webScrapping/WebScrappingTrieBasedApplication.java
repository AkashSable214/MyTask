package com.webScrapping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WebScrappingTrieBasedApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebScrappingTrieBasedApplication.class, args);
	}

}
