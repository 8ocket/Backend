package com.kt.mindLog.repository.session;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.mindLog.domain.session.Session;

public interface SessionRepositoryCustom {
	Page<Session> findSessions (
		UUID userId,
		LocalDate startDate,
		LocalDate endDate,
		List<UUID> personaIds,
		Pageable pageable
	);
}
