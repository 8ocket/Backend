package com.kt.mindLog.controller.session;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.dto.session.request.SessionCreateRequest;
import com.kt.mindLog.dto.session.request.SessionReceiveRequest;
import com.kt.mindLog.global.annotation.Login;
import com.kt.mindLog.global.security.CustomUser;
import com.kt.mindLog.service.session.SessionMessageService;
import com.kt.mindLog.service.session.SessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/sessions")
public class SessionController {

	private final SessionService sessionService;
	private final SessionMessageService sessionMessageService;

	@PostMapping(value ="/{sessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<?>> receiveSSE(@Login CustomUser user, @RequestBody SessionReceiveRequest contents, @PathVariable String sessionId) {
		return sessionMessageService.receiveSSE(contents.contents(), sessionId, user.getId());
	}

	@PostMapping()
	public Flux<ServerSentEvent<?>> createSession(@Login CustomUser user, @Valid @RequestBody SessionCreateRequest request) {
		return sessionService.createSession(user.getId(), request);
	}
}
