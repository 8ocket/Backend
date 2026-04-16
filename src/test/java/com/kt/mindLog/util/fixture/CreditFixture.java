package com.kt.mindLog.util.fixture;

import com.kt.mindLog.domain.credit.Credit;
import com.kt.mindLog.domain.credit.TransactionType;
import com.kt.mindLog.domain.payment.Payment;
import com.kt.mindLog.domain.user.User;

public abstract class CreditFixture {

	public static Credit initCredit(User user) {
		return buildCredit(user);
	}

	protected static Credit buildCredit(User user) {
		var credit = Credit.builder()
			.transactionType(TransactionType.CHARGE)
			.freeCredit(600)
			.paidCredit(0)
			.amount(600)
			.balanceAfter(600)
			.remainingPaidCredit(0)
			.description("initialize")
			.user(user)
			// .payment()
			.build();

		return credit;
	}
}
