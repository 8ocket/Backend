package com.kt.mindLog.controller.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.domain.user.LoginType;
import com.kt.mindLog.dto.oauth.request.LogoutRequest;
import com.kt.mindLog.dto.user.response.LoginResponse;
import com.kt.mindLog.global.common.response.ApiResult;
import com.kt.mindLog.service.auth.AuthService;
import com.kt.mindLog.service.user.JwtService;
import com.kt.mindLog.service.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

	private final AuthService AuthService;
	private final UserService userService;
	private final JwtService jwtService;

	// KAKAO
	@GetMapping("/kakao/callback")
	public LoginResponse kakaoLogin(@RequestParam("code") String code) {
		String accessToken = AuthService.getAccessTokenFromKakao(code);
		String email = AuthService.getKakaoUserInfo(accessToken);

		return userService.login(email, LoginType.KAKAO);
	}

	// GOOGLE
	@GetMapping("/google/callback")
	public LoginResponse googleLogin(@RequestParam("code") String code) {
		String accessToken = AuthService.getAccessTokenFromGoogle(code);
		String email = AuthService.getGoogleUserInfo(accessToken);

		return userService.login(email, LoginType.GOOGLE);
	}

	@GetMapping("/refresh")
	public LoginResponse reissue(@RequestParam String refreshToken) {
		return jwtService.reissueToken(refreshToken);
	}

	@PostMapping("/logout")
	public ApiResult<Void> logout(@RequestBody LogoutRequest request) {
		jwtService.logout(request.refreshToken());

		return ApiResult.ok();
	}


}
