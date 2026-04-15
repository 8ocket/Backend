package com.kt.mindLog.repository.summary;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.summary.SessionSummary;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

@Repository
public interface SummaryRepository extends JpaRepository<SessionSummary, UUID> {
	default SessionSummary findByIdOrThrow(UUID id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	void deleteBySessionId(UUID sessionId);

	Page<SessionSummary> findAllByUserId(UUID userId, Pageable pageable);

	boolean existsBySessionId(UUID sessionId);
}
