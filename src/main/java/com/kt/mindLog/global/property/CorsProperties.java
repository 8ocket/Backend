package com.kt.mindLog.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
	private final String local;
	private final String dev;
	private final String prod;
}
