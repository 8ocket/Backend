package com.kt.mindLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.mindLog.domain.session.SessionMessage;

public interface SessionMessageRepository extends JpaRepository<SessionMessage, Long> {
}
