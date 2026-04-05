package com.kt.mindLog.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ConfigurationProperties(prefix = "toss")
@Getter
@AllArgsConstructor
public class TossProperties {
	private String baseUrl;
	private String clientKey;
	private String secretKey;
}
