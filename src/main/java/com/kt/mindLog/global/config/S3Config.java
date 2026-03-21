package com.kt.mindLog.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.kt.mindLog.global.property.AwsProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class S3Config {

	private final AwsProperties awsProperties;

	@Bean
	public AmazonS3 amazonS3() {
		return AmazonS3ClientBuilder.standard()
			.withRegion(awsProperties.getRegion())
			.build();
	}
}
