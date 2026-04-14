package com.kt.mindLog.domain.payment;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.kt.mindLog.domain.credit.ProductType;
import com.kt.mindLog.domain.session.SessionStatus;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor
public class Payment {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "payment_id")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, unique = true)
	private String orderId;

	@Column(unique = true)
	private String paymentKey;

	@Column(nullable = false)
	private int amount;

	private String orderName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductType productType;

	private LocalDateTime approvedAt;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	public boolean isCanceled() {
		return this.status == PaymentStatus.CANCELED;
	}

	public void complete(LocalDateTime approvedAt) {
		if (this.status != PaymentStatus.READY) {
			throw new CustomException(ErrorCode.PAYMENT_NOT_READY);
		}
		this.status = PaymentStatus.DONE;
		this.approvedAt = approvedAt;
	}

	public void updatePaymentKey(String paymentKey) {
		if (this.paymentKey != null) {
			throw new CustomException(ErrorCode.ALREADY_PROCESSED);
		}
		this.paymentKey = paymentKey;
	}

	public void fail() {
		if (this.status != PaymentStatus.READY) {
			throw new CustomException(ErrorCode.PAYMENT_NOT_READY);
		}
		this.status = PaymentStatus.FAILED;
	}

	public void cancel() {
		if (this.status != PaymentStatus.DONE) {
			throw new CustomException(ErrorCode.PAYMENT_NOT_DONE);
		}
		this.status = PaymentStatus.CANCELED;
	}

	@Builder
	private Payment(User user, String orderId, String paymentKey, int amount, String orderName, ProductType productType) {
		this.user = user;
		this.orderId = orderId;
		this.paymentKey = paymentKey;
		this.amount = amount;
		this.orderName = orderName;
		this.status = PaymentStatus.READY;
		this.createdAt = LocalDateTime.now();
		this.productType = productType;
	}

	public static Payment create(
		User user,
		String orderId,
		int amount,
		String orderName,
		ProductType productType
	) {
		return Payment.builder()
			.user(user)
			.orderId(orderId)
			.amount(amount)
			.orderName(orderName)
			.productType(productType)
			.build();
	}

}
