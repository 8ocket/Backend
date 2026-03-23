package com.kt.mindLog.repository.session;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.mindLog.domain.session.CrisisLogs;

public interface CrisisLogRepository extends JpaRepository<CrisisLogs, UUID> {
}
