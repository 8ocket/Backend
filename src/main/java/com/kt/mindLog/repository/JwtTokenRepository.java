package com.kt.mindLog.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.auth.JwtToken;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, UUID> {
	Optional<JwtToken> findByRefreshToken(String token);

	void deleteByExpiresAtBefore(LocalDateTime now);

	void deleteByRefreshToken(String refreshToken);

	void deleteByUserId(UUID userId);
}
