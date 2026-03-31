package com.kt.mindLog.dto.credit;

public record UserCreditResponse(int totalCredit) {
	public static UserCreditResponse from(int totalCredit) {
		return new UserCreditResponse(totalCredit);
	}
}
