package com.kt.mindLog.domain.payment;

public enum PaymentStatus {
	READY, // 결제 생성
	ABORTED, // 결제 승인이 실패 했을 때 (외부적인 실패)
	DONE, // 결제 성공
	FAILED, // 내부적인 결제 실패
	CANCELED // 결제 취소
}
