package com.kt.mindLog.repository.summary;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.summary.SessionSummary;

@Repository
public interface SummaryRepository extends JpaRepository<SessionSummary, UUID> {
	void deleteBySessionId(UUID sessionId);
}
