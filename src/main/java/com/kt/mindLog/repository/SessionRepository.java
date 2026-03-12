package com.kt.mindLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

public interface SessionRepository extends JpaRepository<Session,String> {
	default Session findByIdOrThrow(String id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}
}
