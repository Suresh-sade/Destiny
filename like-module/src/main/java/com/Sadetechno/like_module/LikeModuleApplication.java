package com.Sadetechno.like_module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LikeModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(LikeModuleApplication.class, args);
	}

}
