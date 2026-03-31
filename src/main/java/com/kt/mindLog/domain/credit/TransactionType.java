package com.kt.mindLog.domain.credit;

public enum TransactionType {
	// 적립
	SIGNUP_BONUS, // 회원가입 보너스 (+150)
	ATTENDANCE_CHECK, // 출석 체크 (+3)
	SAVE_SESSION, // 상담 저장 버튼 클릭 (+3)
	WATCH_AD, // 광고 시청 (+2)
	SURVEY, // 설문 조사 응답 (+5)

	// 사용
	AI_WEEKLY_REPORT, // AI 심화 주간 리포트 (-150)
	AI_MONTHLY_REPORT, // AI 심화 월간 리포트 (-500)
	EXTRA_SESSION, // 1일 1회 무료 상담 소진 이후 상담 생성 (-70)

	// 결제 / 환불
	CHARGE, // 유료 충전
	REFUND
}