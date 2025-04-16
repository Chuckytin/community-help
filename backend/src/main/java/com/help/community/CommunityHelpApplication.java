package com.help.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommunityHelpApplication {
	public static void main(String[] args) {
		System.setProperty("MYSQL_USER", "root");
		System.setProperty("MYSQL_PASSWORD", "tu_password");

		SpringApplication.run(CommunityHelpApplication.class, args);
	}
}