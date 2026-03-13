package com.kt.mindLog.global.common.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kt.mindLog.domain.persona.Persona;
import com.kt.mindLog.repository.PersonaRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Initializer {
	private final PersonaRepository personaRepository;

	@PostConstruct
	@Transactional
	public void init() {
		if (personaRepository.count() > 0) {
			return;
		}

		List<Persona> personas = new ArrayList<>();
		personas.add(new Persona(1L));

		personaRepository.saveAll(personas);
	}
}
