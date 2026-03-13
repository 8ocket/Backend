package com.kt.mindLog.global.security;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.kt.mindLog.domain.user.Role;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.property.JwtProperties;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {

	private final JwtProperties jwtProperties;

	public String createToken(final String userId, final Role role, final Date expired) {
		return Jwts.builder()
			.id(UUID.randomUUID().toString())
			.subject(userId)
			.claim("role", role.toString())
			.issuer("8ocket")
			.expiration(expired)
			.signWith(jwtProperties.getJwtSecretkey())
			.compact();
	}

	public void validateToken(String token) {

		try {
			Jwts.parser()
				.verifyWith(jwtProperties.getJwtSecretkey())
				.build()
				.parseSignedClaims(token);

		} catch (ExpiredJwtException e) {
			throw new CustomException(ErrorCode.EXPIRED_JWT_TOKEN);
		} catch (MalformedJwtException e) {
			throw new CustomException(ErrorCode.INVALID_JWT_TOKEN_FORMAT);
		} catch (JwtException | IllegalArgumentException e) {
			throw new CustomException(ErrorCode.INVALID_JWT_TOKEN_FORMAT);
		}
	}

	public CustomUser getUserDetail(String token) {
		var claim = Jwts.parser()
			.verifyWith(jwtProperties.getJwtSecretkey())
			.build()
			.parseSignedClaims(token)
			.getPayload();

		var id = String.valueOf(claim.getSubject());
		var role = Role.valueOf(claim.get("role").toString());

		return new CustomUser(id, role);
	}
}
