package com.kt.mindLog.repository.summary;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.mindLog.domain.summary.SessionSummary;

public interface SummaryRepository extends JpaRepository<SessionSummary, UUID> {
}
