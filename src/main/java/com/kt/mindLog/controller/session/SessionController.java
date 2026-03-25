package com.kt.mindLog.controller.session;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.dto.session.request.SessionCreateRequest;
import com.kt.mindLog.dto.session.request.SessionReceiveRequest;
import com.kt.mindLog.dto.session.response.ActiveSessionResponse;
import com.kt.mindLog.dto.session.response.SessionDetailResponse;
import com.kt.mindLog.dto.session.response.SessionListResponses;
import com.kt.mindLog.dto.session.response.SessionResponse;
import com.kt.mindLog.global.annotation.Login;
import com.kt.mindLog.global.common.request.Paging;
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
	public Flux<Object> receiveSSE(@Login CustomUser user, @Valid @RequestBody SessionReceiveRequest content,
		@PathVariable UUID sessionId) {
		return sessionMessageService.receiveSSE(content.content(), sessionId, user.getId());
	}

	@PostMapping("")
	public SessionResponse createSession(@Login CustomUser user, @Valid @RequestBody SessionCreateRequest request) {
		return sessionService.saveSession(user.getId(), request);
	}

	@GetMapping("")
	public SessionListResponses getSessions(@Login CustomUser user,
		@RequestParam(required = false) LocalDate startDate,
		@RequestParam(required = false) LocalDate endDate,
		@RequestParam(required = false) List<UUID> personaIds,
		Paging paging) {
		return sessionService.getSessionList(user.getId(), paging.toPageable(), startDate, endDate, personaIds);
	}

	@GetMapping("/{sessionId}")
	public SessionDetailResponse getSessionDetail(@Login CustomUser user, @PathVariable UUID sessionId) {
		return sessionService.getSessionDetail(user.getId(), sessionId);
	}

	@GetMapping("/active")
	public ActiveSessionResponse getActiveSession(@Login CustomUser user) {
		return sessionService.getActiveSession(user.getId());
	}

	@PostMapping(value = "/{sessionId}/finalize", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Object> finalizeSession(@Login CustomUser user, @PathVariable UUID sessionId) {
		return sessionMessageService.finalizeSession(sessionId, user.getId());
	}
}
