package com.kt.mindLog.domain.payment;

public enum PaymentStatus {
	READY, // 결제 생성
	IN_PROGRESS, // 결제 승인 요청 보냈을 때
	DONE, // 결제 성공
	FAILED, // 결제 실패
	CANCELED // 결제 취소
}
