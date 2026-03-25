package com.kt.mindLog.service.persona;

import java.util.UUID;

import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kt.mindLog.domain.persona.Persona;
import com.kt.mindLog.domain.user.Role;
import com.kt.mindLog.dto.persona.request.PersonaCreateRequest;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;
import com.kt.mindLog.global.security.CustomUser;
import com.kt.mindLog.repository.PersonaRepository;
import com.kt.mindLog.service.s3.S3Path;
import com.kt.mindLog.service.s3.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonaService {
	private final PersonaRepository personaRepository;
	private final S3Service s3Service;
	private final ObjectMapper objectMapper;

	@Transactional
	public UUID createPersona(final Role role, final PersonaCreateRequest request, final MultipartFile personaFile) {
		Preconditions.validate(role.equals(Role.ADMIN), ErrorCode.INVALID_USER);
		Preconditions.validate(!personaRepository.existsByPersonaName(request.personaName()), ErrorCode.INVALID_PERSONA);

		var toneSettingJson = objectMapper.writeValueAsString(request.toneSettings());

		var persona = Persona.builder()
			.personaType(request.personaType())
			.personaName(request.personaName())
			.description(request.description())
			.toneSettings(toneSettingJson)
			.unlockCredits(request.unlockCredits())
			.build();

		var personaImageUrl = s3Service.uploadImage(personaFile, S3Path.PERSONA);
		persona.updatePersonaImageUrl(personaImageUrl);
		personaRepository.save(persona);

		log.info("persona created successfully");
		return persona.getId();
	}
}
