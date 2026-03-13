package com.kt.mindLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.mindLog.domain.persona.Persona;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

public interface PersonaRepository extends JpaRepository<Persona, String> {
	default Persona findByIdOrThrow(String id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}
}
