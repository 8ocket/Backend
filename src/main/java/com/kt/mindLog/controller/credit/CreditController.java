package com.kt.mindLog.controller.credit;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.dto.credit.CreditProductResponse;
import com.kt.mindLog.dto.credit.UserCreditResponse;
import com.kt.mindLog.global.annotation.Login;
import com.kt.mindLog.global.common.response.ApiResult;
import com.kt.mindLog.global.security.auth.CustomUser;
import com.kt.mindLog.service.credit.CreditService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/credits")
public class CreditController {
	private final CreditService creditService;

	@GetMapping("/me")
	public ApiResult<UserCreditResponse> getCredits(@Login CustomUser user) {
		return ApiResult.ok(creditService.getCredits(user.getId()));
	}

	@GetMapping
	public ApiResult<List<CreditProductResponse>> getCreditProducts() {
		return ApiResult.ok(creditService.getCreditProducts());
	}

}
