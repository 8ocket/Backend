package com.kt.mindLog.service.auth;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.kt.mindLog.dto.oauth.response.KakaoTokenResponse;
import com.kt.mindLog.dto.oauth.response.KakaoUserInfoResponse;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;
import com.kt.mindLog.global.property.KakaoProperties;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AuthService {
	private final KakaoProperties kakaoProperties;
	private final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
	private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

	// KAKAO
	public String getAccessTokenFromKakao(String code) {
		KakaoTokenResponse response = WebClient.create(KAUTH_TOKEN_URL_HOST)
			.post()
			.uri("/oauth/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(BodyInserters.fromFormData("grant_type", "authorization_code")
				.with("client_id", kakaoProperties.getClientId())
				.with("client_secret", kakaoProperties.getClientSecret())
				.with("redirect_uri", kakaoProperties.getRedirectUri())
				.with("code", code))
			.retrieve()
			.onStatus(HttpStatusCode::isError,
				ClientResponse -> Mono.error(new CustomException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED)))
			.bodyToMono(KakaoTokenResponse.class)
			.block();

		return response.getAccessToken();
	}

	public String getUserInfo(String accessToken) {
		KakaoUserInfoResponse userInfo = WebClient.create(KAUTH_USER_URL_HOST)
			.get()
			.uri("/v2/user/me")
			.header("Authorization", "Bearer " + accessToken)
			.retrieve()
			.bodyToMono(KakaoUserInfoResponse.class)
			.block();

		Preconditions.validate(userInfo != null, ErrorCode.KAKAO_USER_INFO_ERROR);
		Preconditions.validate(userInfo.getKakaoAccount() != null, ErrorCode.KAKAO_USER_INFO_ERROR);

		String email = userInfo.getKakaoAccount().getEmail();

		Preconditions.validate(email != null, ErrorCode.KAKAO_USER_INFO_ERROR);

		return email;
	}




}
