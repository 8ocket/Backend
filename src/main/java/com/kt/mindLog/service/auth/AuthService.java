package com.kt.mindLog.service.auth;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.kt.mindLog.dto.oauth.response.GoogleTokenResponse;
import com.kt.mindLog.dto.oauth.response.GoogleUserInfoResponse;
import com.kt.mindLog.dto.oauth.response.KakaoTokenResponse;
import com.kt.mindLog.dto.oauth.response.KakaoUserInfoResponse;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;
import com.kt.mindLog.global.property.GoogleProperties;
import com.kt.mindLog.global.property.KakaoProperties;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AuthService {
	private final KakaoProperties kakaoProperties;
	private final GoogleProperties googleProperties;

	private static final String AUTHORIZATION = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	// OAuth hosts
	private static final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
	private static final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

	private static final String GOOGLE_TOKEN_URL_HOST = "https://oauth2.googleapis.com";
	private static final String GOOGLE_USER_URL_HOST = "https://www.googleapis.com";

	// OAuth endpoints
	private static final String KAKAO_TOKEN_URI = "/oauth/token";
	private static final String KAKAO_USER_URI = "/v2/user/me";

	private static final String GOOGLE_TOKEN_URI = "/token";
	private static final String GOOGLE_USER_URI = "/oauth2/v3/userinfo";

	// OAuth WebClients
	private final WebClient kakaoAuthClient = WebClient.create(KAUTH_TOKEN_URL_HOST);
	private final WebClient kakaoApiClient = WebClient.create(KAUTH_USER_URL_HOST);
	private final WebClient googleAuthClient = WebClient.create(GOOGLE_TOKEN_URL_HOST);
	private final WebClient googleApiClient = WebClient.create(GOOGLE_USER_URL_HOST);

	// KAKAO
	public String getAccessTokenFromKakao(String code) {
		KakaoTokenResponse response = kakaoAuthClient
			.post()
			.uri(KAKAO_TOKEN_URI)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(BodyInserters.fromFormData("grant_type", "authorization_code")
				.with("client_id", kakaoProperties.getClientId())
				.with("client_secret", kakaoProperties.getClientSecret())
				.with("redirect_uri", kakaoProperties.getRedirectUri())
				.with("code", code))
			.retrieve()
			.onStatus(HttpStatusCode::isError,
				clientResponse -> Mono.error(new CustomException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED)))
			.bodyToMono(KakaoTokenResponse.class)
			.block();

		return response.accessToken();
	}

	public String getKakaoUserInfo(String accessToken) {
		KakaoUserInfoResponse userInfo = kakaoApiClient
			.get()
			.uri(KAKAO_USER_URI)
			.header(AUTHORIZATION, BEARER_PREFIX + accessToken)
			.retrieve()
			.bodyToMono(KakaoUserInfoResponse.class)
			.block();

		Preconditions.validate(userInfo != null, ErrorCode.KAKAO_USER_INFO_ERROR);
		Preconditions.validate(userInfo.kakaoAccount() != null, ErrorCode.KAKAO_USER_INFO_ERROR);

		String email = userInfo.kakaoAccount().email();

		Preconditions.validate(email != null, ErrorCode.KAKAO_USER_INFO_ERROR);

		return email;
	}

	// GOOGLE
	public String getAccessTokenFromGoogle(String code) {
		GoogleTokenResponse response = googleAuthClient
			.post()
			.uri(GOOGLE_TOKEN_URI)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(BodyInserters.fromFormData("grant_type", "authorization_code")
				.with("client_id", googleProperties.getClientId())
				.with("client_secret", googleProperties.getClientSecret())
				.with("redirect_uri", googleProperties.getRedirectUri())
				.with("code", code))
			.retrieve()
			.onStatus(HttpStatusCode::isError,
				clientResponse -> Mono.error(new CustomException(ErrorCode.GOOGLE_TOKEN_REQUEST_FAILED)))
			.bodyToMono(GoogleTokenResponse.class)
			.block();

		return response.accessToken();
	}

	public String getGoogleUserInfo(String accessToken) {
		GoogleUserInfoResponse userInfo = googleApiClient
			.get()
			.uri(GOOGLE_USER_URI)
			.header(AUTHORIZATION, BEARER_PREFIX + accessToken)
			.retrieve()
			.onStatus(HttpStatusCode::isError,
				clientResponse -> Mono.error(new CustomException(ErrorCode.GOOGLE_USER_INFO_ERROR)))
			.bodyToMono(GoogleUserInfoResponse.class)
			.block();

		Preconditions.validate(userInfo != null, ErrorCode.GOOGLE_USER_INFO_ERROR);

		String email = userInfo.email();

		Preconditions.validate(email != null, ErrorCode.GOOGLE_USER_INFO_ERROR);

		return email;
	}
}
