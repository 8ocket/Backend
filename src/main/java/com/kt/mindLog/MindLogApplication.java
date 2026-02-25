package com.kt.mindLog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MindLogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MindLogApplication.class, args);
	}

}
