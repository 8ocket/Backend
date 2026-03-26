package com.kt.mindLog.repository.persona;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.mindLog.domain.user.UserPersona;

public interface UserPersonaRepository extends JpaRepository<UserPersona, UUID> {
	List<UserPersona> findByUserId(UUID userId);
}
