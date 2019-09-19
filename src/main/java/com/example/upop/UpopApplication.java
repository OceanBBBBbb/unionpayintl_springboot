package com.example.upop;

import com.example.upop.config.AutoLoadServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;

@SpringBootApplication
@EnableConfigurationProperties
@EnableAutoConfiguration
public class UpopApplication {

	public static void main(String[] args) {
		SpringApplication.run(UpopApplication.class, args);
	}

}
