package com.kt.mindLog.repository.session;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

public interface SessionRepository extends JpaRepository<Session,UUID> {
	default Session findByIdOrThrow(UUID id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	@EntityGraph(attributePaths = "persona")
	Page<Session> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
