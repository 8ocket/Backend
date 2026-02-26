package com.kt.mindLog.service.auth;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.auth.JwtToken;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.user.response.LoginResponse;
import com.kt.mindLog.global.property.JwtProperties;
import com.kt.mindLog.repository.JwtTokenRepository;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

	private final JwtProperties jwtProperties;
	private final JwtTokenRepository jwtTokenRepository;

	@Transactional
	public LoginResponse createJwtTokens(User user, boolean isNewUser) {
		String accessToken = createToken(user.getId(), jwtProperties.getAccessTokenExp());
		String refreshToken = createToken(user.getId(), jwtProperties.getRefreshTokenExp());

		LocalDateTime expiresAt = jwtProperties.getRefreshTokenExp().toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDateTime();

		JwtToken jwtToken = JwtToken.builder()
			.user(user)
			.refreshToken(refreshToken)
			.expiresAt(expiresAt)
			.build();

		jwtTokenRepository.save(jwtToken);

		return LoginResponse.of(accessToken, refreshToken, isNewUser);
	}

	private String createToken(final Long userId, final Date expired) {

		return Jwts.builder()
			.id(UUID.randomUUID().toString())
			.subject(userId.toString())
			.issuer("8ocket")
			.expiration(expired)
			.signWith(jwtProperties.getJwtSecretkey())
			.compact();
	}
}
