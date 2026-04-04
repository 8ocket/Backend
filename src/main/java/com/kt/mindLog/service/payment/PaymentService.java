package com.kt.mindLog.service.payment;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.payment.Payment;
import com.kt.mindLog.global.client.TossClient;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;
import com.kt.mindLog.repository.payment.PaymentRepository;
import com.kt.mindLog.service.credit.CreditService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final CreditService creditService;
	private final PaymentRepository paymentRepository;

	private final TossClient tossClient;

	@Transactional
	public void refund(UUID paymentId) {
		Payment payment = paymentRepository.findByIdOrThrow(paymentId, ErrorCode.NOT_FOUND_PAYMENT);

		// 이미 환불된 결제인지 검증
		Preconditions.validate(!payment.isCanceled(), ErrorCode.ALREADY_CANCELED);

		// 이미 사용된 크레딧 있는지 검증
		creditService.validateRefundable(paymentId);

		// 토스 환불
		// tossClient.refund(payment.getPaymentKey());

		// 크레딧 회수
		creditService.revokePaidCredits(paymentId);

		// 결제 상태 변경
		payment.cancel();
	}
}
