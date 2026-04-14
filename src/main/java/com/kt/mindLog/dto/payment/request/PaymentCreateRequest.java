package com.kt.mindLog.dto.payment.request;

import com.kt.mindLog.domain.credit.ProductType;

public record PaymentCreateRequest(
	ProductType productType
) {
}
