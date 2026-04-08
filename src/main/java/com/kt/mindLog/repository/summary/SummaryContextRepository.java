package com.kt.mindLog.repository.summary;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.summary.SessionContextSummary;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

@Repository
public interface SummaryContextRepository extends JpaRepository<SessionContextSummary, UUID> {
	Optional<SessionContextSummary> findBySessionId(UUID sessionId);

	default SessionContextSummary findBySessionIdOrThrow(UUID sessionId, ErrorCode errorCode) {
		return findBySessionId(sessionId).orElseThrow(() -> new CustomException(errorCode));
	}

	void deleteBySessionId(UUID sessionId);
}
