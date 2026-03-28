package com.kt.mindLog.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ConfigurationProperties(prefix = "kakao")
@AllArgsConstructor
@Getter
public class KakaoProperties {
	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;

	public String getRedirectUri() {
		return "http://localhost:8080/v1/auth/kakao/callback";
	}
}
