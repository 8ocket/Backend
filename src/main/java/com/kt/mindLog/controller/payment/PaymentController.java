package com.kt.mindLog.controller.payment;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.global.common.response.ApiResult;
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


}
