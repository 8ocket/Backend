package com.kt.mindLog.controller.payment;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.dto.payment.request.PaymentConfirmRequest;
import com.kt.mindLog.dto.payment.request.PaymentCreateRequest;
import com.kt.mindLog.dto.payment.response.PaymentCreateResponse;
import com.kt.mindLog.dto.payment.response.PaymentListResponse;
import com.kt.mindLog.global.annotation.Login;
import com.kt.mindLog.global.common.response.ApiResult;
import com.kt.mindLog.global.security.CustomUser;
import com.kt.mindLog.service.payment.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentController {
	private final PaymentService paymentService;

	@PostMapping("/{paymentId}/cancel")
	public ApiResult<UUID> refund(@PathVariable UUID paymentId) {
		paymentService.refund(paymentId);

		return ApiResult.ok(paymentId);
	}

	@GetMapping("/history")
	public ApiResult<Page<PaymentListResponse>> getPaymentList(@Login CustomUser user, Pageable pageable) {
		return ApiResult.ok(paymentService.getPaymentList(user.getId(), pageable));
	}

	@PostMapping("/confirm")
	public ApiResult<Void> confirmPayment(@RequestBody PaymentConfirmRequest request) {
		paymentService.confirm(request);
		return ApiResult.ok();
	}

	@PostMapping
	public ApiResult<PaymentCreateResponse> create(
		@AuthenticationPrincipal CustomUser user, @RequestBody PaymentCreateRequest request) {
		return ApiResult.ok(paymentService.create(user.getId(), request));
	}
}
