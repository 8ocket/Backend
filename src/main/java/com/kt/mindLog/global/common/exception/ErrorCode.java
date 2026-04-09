package com.kt.mindLog.global.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// KAKAO OAuth
	KAKAO_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "카카오 액세스 토큰 발급에 실패했습니다."),
	KAKAO_USER_INFO_ERROR(HttpStatus.BAD_REQUEST, "카카오 유저 정보를 정상적으로 가져오지 못했습니다."),

	// GOOGLE OAuth
	GOOGLE_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "구글 액세스 토큰 발급에 실패했습니다."),
	GOOGLE_USER_INFO_ERROR(HttpStatus.BAD_REQUEST, "구글 유저 정보를 정상적으로 가져오지 못했습니다."),

	// auth
	INVALID_JWT_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 접근입니다. 다시 로그인을 시도해주시기 바랍니다."),
	EXPIRED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "만료된 토큰입니다. 다시 로그인을 시도해주시기 바랍니다."),
	INVALID_JWT_TOKEN_FORMAT(HttpStatus.UNAUTHORIZED, "잘못된 형식의 JWT 토큰입니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 리프레시 토큰입니다."),
	INVALID_CRYPTO_KEY(HttpStatus.BAD_REQUEST, "암호화 키는 32바이트(256bit)여야 합니다."),

	// user
	NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."),
	INVALID_USER(HttpStatus.UNAUTHORIZED, "접근 권한이 없는 회원입니다."),
	INVALID_NICKNAME_CHANGE(HttpStatus.BAD_REQUEST, "닉네임 교체는 매월 3회까지 가능합니다."),
	SAME_NICKNAME_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "이전의 닉네임과 동일합니다."),

	// session
	NOT_FOUND_SESSION(HttpStatus.BAD_REQUEST, "존재하지 않는 세션입니다."),
	NOT_FOUND_SESSION_MESSAGE(HttpStatus.BAD_REQUEST, "존재하지 않는 상담 내용입니다."),
	INVALID_SESSION(HttpStatus.BAD_REQUEST, "만료된 상담입니다. 새로운 상담을 시작해주세요"),

	// persona
	NOT_FOUND_PERSONA(HttpStatus.BAD_REQUEST, "존재하지 않는 페르소나입니다."),
	INVALID_PERSONA(HttpStatus.BAD_REQUEST, "이미 존재하는 페르소나 이름입니다."),

	// Amazon S3
	EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 비어 있습니다."),
	INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드 가능합니다."),
	INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "허용되지 않는 파일 확장자입니다."),
	FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기는 5MB를 초과 할 수 없습니다."),
	FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "파일 업로드에 실패했습니다."),

	// summary
	INVALID_SESSION_SUMMARY(HttpStatus.BAD_REQUEST, "이미 분석 완료된 상담 세션입니다."),
	NOT_FOUND_SUMMARY(HttpStatus.BAD_REQUEST, "해당 세션 요약 컨텍스트를 찾을 수 없습니다."),
	NOT_SUMMARY_USER(HttpStatus.BAD_REQUEST, "본인 소유 세션만 사용 가능합니다."),

	// credit
	ALREADY_USED_CREDIT(HttpStatus.BAD_REQUEST, "이미 사용된 크레딧입니다."),
	INSUFFICIENT_CREDIT(HttpStatus.BAD_REQUEST, "크레딧이 부족합니다."),

	// payment
	NOT_FOUND_PAYMENT(HttpStatus.BAD_REQUEST, "결제 정보를 찾을 수 없습니다."),
	ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "이미 취소된 결제입니다."),
	PAYMENT_ORDER_ID_MISMATCH(HttpStatus.BAD_REQUEST, "결제 정보가 올바르지 않습니다."),
	PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다."),
	PAYMENT_NOT_READY(HttpStatus.BAD_REQUEST, "결제가 준비 상태가 아닙니다."),
	PAYMENT_NOT_DONE(HttpStatus.BAD_REQUEST, "결제가 완료된 상태가 아닙니다."),
	ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "이미 처리된 결제입니다."),
	TOSS_CONFIRM_FAILED(HttpStatus.BAD_REQUEST, "토스 결제 승인에 실패했습니다."),
	TOSS_REFUND_FAILED(HttpStatus.BAD_REQUEST, "토스 결제 환불에 실패했습니다."),

	//report
	INSUFFICIENT_SESSIONS(HttpStatus.BAD_REQUEST, "최소 상담 기록 횟수가 부족합니다"),
	REPORT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "동일 기간에 대해 생성한 AI 리포트가 이미 존재합니다"),
	NOT_FOUND_REPORT(HttpStatus.BAD_REQUEST, "존재하지 않는 AI 리포트입니다"),

	//encrypt
	ENCRYPTION_FAILED(HttpStatus.BAD_REQUEST, "텍스트 암호화에 실패하였습니다"),
	DECRYPTION_FAILED(HttpStatus.BAD_REQUEST, "텍스트 복호화에 실패하였습니다"),

	// emotion
	NOT_FOUND_CARD(HttpStatus.BAD_REQUEST, "감정 카드를 찾을 수 없습니다.")

	;

	private final HttpStatus status;
	private final String message;
}