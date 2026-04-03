package com.kt.mindLog.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.persona.Persona;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, UUID> {
	default Persona findByIdOrThrow(UUID id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	boolean existsByPersonaName(String personaName);

	Optional<Persona> findByIsDefault(boolean isDefault);

	default Persona findByIsDefaultOrThrow(boolean isDefault, ErrorCode errorCode) {
		return findByIsDefault(isDefault).orElseThrow(() -> new CustomException(errorCode));
	}
}
