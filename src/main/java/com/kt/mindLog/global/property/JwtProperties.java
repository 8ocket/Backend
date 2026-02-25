package com.kt.mindLog.global.property;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
	private final String secretkey;
	private Long accessTokenExp;
	private Long refreshTokenExp;

	public Date getAccessTokenExp() {
		return new Date(new Date().getTime() + accessTokenExp);
	}

	public Date getRefreshTokenExp() {
		return new Date(new Date().getTime() + refreshTokenExp);
	}

	public SecretKey getJwtSecretkey() {
		return Keys.hmacShaKeyFor(secretkey.getBytes());
	}
}
