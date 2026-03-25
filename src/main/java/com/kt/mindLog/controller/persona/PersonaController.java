package com.kt.mindLog.controller.persona;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kt.mindLog.dto.persona.request.PersonaCreateRequest;
import com.kt.mindLog.global.annotation.Login;
import com.kt.mindLog.global.common.response.ApiResult;
import com.kt.mindLog.global.security.CustomUser;
import com.kt.mindLog.service.persona.PersonaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/personas")
public class PersonaController {
	private final PersonaService personaService;

	@PostMapping("")
	public ApiResult<?> createPersona(@Login CustomUser user, @Valid @RequestPart("contents") PersonaCreateRequest personaCreateRequest,
		@RequestPart("persona_image") MultipartFile personaImage) {
		return ApiResult.ok(personaService.createPersona(user.getRole(), personaCreateRequest, personaImage));
	}
}
