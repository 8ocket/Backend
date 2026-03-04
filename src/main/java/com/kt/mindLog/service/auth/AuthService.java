package com.kt.mindLog.service.auth;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.auth.JwtToken;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.user.LoginResponse;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.property.JwtProperties;
import com.kt.mindLog.global.security.JwtProvider;
import com.kt.mindLog.repository.JwtTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

	private final JwtProperties jwtProperties;
	private final JwtProvider jwtProvider;
	private final JwtTokenRepository jwtTokenRepository;

	public LoginResponse createJwtTokens(User user, Boolean isNewUser) {
		String accessToken = jwtProvider.createToken(user.getId(), user.getRole(), jwtProperties.getAccessTokenExp());
		String refreshToken = jwtProvider.createToken(user.getId(), user.getRole(), jwtProperties.getRefreshTokenExp());

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

	@Transactional
	public LoginResponse reissueToken(String token) {

		jwtProvider.validateToken(token);

		Optional<JwtToken> jwtToken = jwtTokenRepository.findByRefreshToken(token);
		if (jwtToken.isEmpty()) {
			throw new CustomException(ErrorCode.INVALID_JWT_TOKEN_FORMAT);
		}

		LoginResponse reissueToken = createJwtTokens(jwtToken.get().getUser(), null);
		jwtTokenRepository.delete(jwtToken.get());

		return reissueToken;
	}

}
