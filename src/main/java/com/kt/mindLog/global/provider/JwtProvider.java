package com.kt.mindLog.global.provider;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.kt.mindLog.global.property.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {

	private final JwtProperties jwtProperties;

	public String createToken(final Long userId, final Date expired) {
		return Jwts.builder()
			.id(UUID.randomUUID().toString())
			.subject(userId.toString())
			.issuer("8ocket")
			.expiration(expired)
			.signWith(jwtProperties.getJwtSecretkey())
			.compact();
	}

	public boolean validateToken(String token) {

		try {
			Jws<Claims> claims = Jwts.parser()
				.verifyWith(jwtProperties.getJwtSecretkey())
				.build()
				.parseSignedClaims(token);

			return claims.getPayload().getExpiration().after(new Date());

		} catch (JwtException e) {
			return false;
		}
	}
}
