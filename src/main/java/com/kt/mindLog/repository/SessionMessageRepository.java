package com.kt.mindLog.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.mindLog.domain.session.SessionMessages;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

public interface SessionMessageRepository extends JpaRepository<SessionMessages, UUID> {
	default SessionMessages findByIdOrThrow(UUID id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	List<SessionMessages> findBySessionIdOrderByCreatedAtDesc(UUID sessionId);
}