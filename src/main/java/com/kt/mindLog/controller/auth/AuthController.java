package com.kt.mindLog.controller.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.domain.enums.LoginType;
import com.kt.mindLog.dto.user.response.LoginResponse;
import com.kt.mindLog.service.auth.AuthService;
import com.kt.mindLog.service.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

	private final AuthService authService;

	@GetMapping("/refresh")
	public LoginResponse reissue(@RequestParam String refreshToken) {
		return authService.reissueToken(refreshToken);
	}


	//for test
	private final UserService userService;

	@GetMapping
	public LoginResponse login(@RequestParam String email, @RequestParam LoginType loginType) {
		return userService.login(email, loginType);
	}
}
