package com.kt.mindLog.repository.session;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.session.CrisisLogs;

@Repository
public interface CrisisLogRepository extends JpaRepository<CrisisLogs, UUID> {
	void deleteBySessionId(UUID sessionId);
}
