package com.kt.mindLog.controller.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.dto.user.LoginResponse;
import com.kt.mindLog.service.user.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class UserController {

	private final JwtService authService;

	@GetMapping("/refresh")
	public LoginResponse reissue(@RequestParam String refreshToken) {
		return authService.reissueToken(refreshToken);
	}
}
