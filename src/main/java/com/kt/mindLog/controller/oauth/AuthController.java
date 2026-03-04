package com.kt.mindLog.controller.oauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.domain.enums.LoginType;
import com.kt.mindLog.dto.user.LoginResponse;
import com.kt.mindLog.service.oauth.AuthService;
import com.kt.mindLog.service.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth/kakao")
public class AuthController {

	private final AuthService authService;
	private final UserService userService;

	// KAKAO
	@GetMapping("/callback")
	public LoginResponse callback(@RequestParam("code") String code) {

		String accessToken = authService.getAccessTokenFromKakao(code);
		String email = authService.getUserInfo(accessToken);

		return userService.login(email, LoginType.KAKAO);
	}
}
