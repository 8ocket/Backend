package com.kt.mindLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.mindLog.domain.session.SessionMessages;

public interface SessionMessageRepository extends JpaRepository<SessionMessages, String> {
}
