package com.kt.mindLog.repository.session;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.session.SessionStatus;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

public interface SessionRepository extends JpaRepository<Session,UUID> {
	default Session findByIdOrThrow(UUID id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	@EntityGraph(attributePaths = "persona")
	Optional<Session> findByIdAndUserId(UUID id, UUID userId);

	default Session findByIdAndUserIdOrThrow(UUID id, UUID userId, ErrorCode errorCode) {
		return findByIdAndUserId(id, userId).orElseThrow(() -> new CustomException(errorCode));
	}

	Optional<Session> findFirstByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, SessionStatus status);
}
