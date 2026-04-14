package com.kt.mindLog.service.payment;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.credit.ProductType;
import com.kt.mindLog.domain.payment.Payment;
import com.kt.mindLog.domain.payment.PaymentStatus;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.payment.request.PaymentConfirmRequest;
import com.kt.mindLog.dto.payment.request.PaymentCreateRequest;
import com.kt.mindLog.dto.payment.response.PaymentConfirmResponse;
import com.kt.mindLog.dto.payment.response.PaymentCreateResponse;
import com.kt.mindLog.dto.payment.response.PaymentListResponse;
import com.kt.mindLog.dto.payment.response.TossPaymentResponse;
import com.kt.mindLog.global.client.TossClient;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.repository.payment.PaymentRepository;
import com.kt.mindLog.service.credit.CreditService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final CreditService creditService;
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;

	private final TossClient tossClient;

	@Transactional
	public void refund(UUID paymentId) {
		Payment payment = paymentRepository.findByIdOrThrow(paymentId, ErrorCode.NOT_FOUND_PAYMENT);

		// 이미 환불된 결제인지 검증
		Preconditions.validate(!payment.isCanceled(), ErrorCode.ALREADY_CANCELED);

		// 이미 사용된 크레딧 있는지 검증
		creditService.validateRefundable(paymentId);

		// 토스 환불
		tossClient.refund(payment.getPaymentKey());

		// 크레딧 회수
		creditService.revokePaidCredits(paymentId);

		// 결제 상태 변경
		payment.cancel();
	}

	public Page<PaymentListResponse> getPaymentList(UUID userId, Pageable pageable) {
		User user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		Page<Payment> payments = paymentRepository.findByUserId(userId, pageable);
		return payments.map(PaymentListResponse::from);
	}

	@Transactional
	public PaymentConfirmResponse confirm(PaymentConfirmRequest request) {
		Payment payment = paymentRepository.findByOrderId(request.orderId())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT));

		// 중복 결제 여부 검증
		Preconditions.validate(
			payment.getStatus() == PaymentStatus.READY,
			ErrorCode.PAYMENT_NOT_READY
		);

		// paymentKey 중복 여부 검증
		Preconditions.validate(
			payment.getPaymentKey() == null,
			ErrorCode.ALREADY_PROCESSED
		);

		// 토스에 결제 승인 요청
		TossPaymentResponse response = tossClient.confirm(
			request.paymentKey(),
			request.orderId(),
			request.amount()
		);

		// 토스 결제 상태 검증
		Preconditions.validate(
			"DONE".equals(response.status()),
			ErrorCode.PAYMENT_NOT_DONE
		);

		// orderId 일치 여부 검증
		Preconditions.validate(
			response.orderId().equals(request.orderId()),
			ErrorCode.PAYMENT_ORDER_ID_MISMATCH
		);

		// amount 일치 여부 검증 (요청 vs 응답)
		Preconditions.validate(
			response.totalAmount() == request.amount(),
			ErrorCode.PAYMENT_AMOUNT_MISMATCH
		);

		// amount 일치 여부 검증 (DB vs 응답)
		Preconditions.validate(
			payment.getAmount() == response.totalAmount(),
			ErrorCode.PAYMENT_AMOUNT_MISMATCH
		);

		// paymentKey 저장 및 결제 상태 완료로 변경
		payment.updatePaymentKey(response.paymentKey());
		payment.complete(response.approvedAt().toLocalDateTime());

		// 크레딧 충전
		creditService.chargePaidCredit(
			payment.getUser(),
			payment
		);

		return PaymentConfirmResponse.from(response);
	}

	@Transactional
	public PaymentCreateResponse create(UUID userId, PaymentCreateRequest request) {
		User user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		ProductType productType = request.productType();
		int amount = productType.getPrice();
		String orderName = productType.getTitle();

		String orderId = "order_" + UUID.randomUUID().toString();

		Payment payment = Payment.create(
			user,
			orderId,
			amount,
			orderName,
			productType
		);

		paymentRepository.save(payment);

		return PaymentCreateResponse.of(
			orderId,
			amount,
			orderName
		);
	}
}
