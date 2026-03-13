package com.kt.mindLog.domain.session;

public enum SessionStatus {
	ACTIVE,     // 상담 진행 중
	COMPLETED,  // 정상 종료
	EXPIRED    // 일정 시간 미사용으로 종료
}
