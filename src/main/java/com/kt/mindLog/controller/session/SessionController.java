package com.kt.mindLog.controller.session;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.global.annotation.Login;
import com.kt.mindLog.global.security.CustomUser;
import com.kt.mindLog.service.session.SessionService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/sessions")
public class SessionController {

	private final SessionService sessionService;

	@PostMapping(value ="/{sessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<?>> receiveSSE(@Login CustomUser user, @RequestBody String contents, @PathVariable Long sessionId) {
		return sessionService.receiveSSE(contents, sessionId, user.getId());
	}
}
