package com.kt.mindLog.controller.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.domain.enums.LoginType;
import com.kt.mindLog.dto.user.LoginResponse;
import com.kt.mindLog.service.auth.AuthService;
import com.kt.mindLog.service.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

	private final AuthService AuthService;
	private final UserService userService;

	// KAKAO
	@GetMapping("/kakao/callback")
	public LoginResponse callback(@RequestParam("code") String code) {

		String accessToken = AuthService.getAccessTokenFromKakao(code);
		String email = AuthService.getUserInfo(accessToken);

		return userService.login(email, LoginType.KAKAO);
	}
}
