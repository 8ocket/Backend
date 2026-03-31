package com.kt.mindLog.domain.credit;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.kt.mindLog.domain.payment.Payment;
import com.kt.mindLog.domain.user.User;

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
@Table(name = "credits")
@NoArgsConstructor
public class Credit {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "credit_id")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id")
	private Payment payment;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionType transactionType;

	@Column(nullable = false)
	private int freeCredit;

	@Column(nullable = false)
	private int paidCredit;

	@Column(nullable = false)
	private int amount; // 크레딧 사용 금액

	@Column(nullable = false)
	private int balanceAfter; // 거래 후 잔액 (무료 + 유료 크레딧)

	@Column(nullable = false)
	private LocalDateTime createdAt;

	private String description;

	@Builder
	private Credit(User user, Payment payment, TransactionType transactionType, int freeCredit, int paidCredit,
		int amount, int balanceAfter, String description) {
		this.user = user;
		this.payment = payment;
		this.transactionType = transactionType;
		this.freeCredit = freeCredit;
		this.paidCredit = paidCredit;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.description = description;
		this.createdAt = LocalDateTime.now();
	}

	public static Credit earnFreeCredit(
		User user,
		TransactionType transactionType,
		int amount,
		int balanceAfter,
		String description
	) {
		return Credit.builder()
			.user(user)
			.transactionType(transactionType)
			.amount(amount)
			.freeCredit(amount)
			.paidCredit(0)
			.balanceAfter(balanceAfter)
			.description(description)
			.build();
	}

	public static Credit useCredit(
		User user,
		TransactionType transactionType,
		int amount,
		int freeUsed, // 차감할 무료 크레딧
		int paidUsed, // 차감할 유료 크레딧
		int balanceAfter,
		String description
	){
		return Credit.builder()
			.user(user)
			.transactionType(transactionType)
			.freeCredit(-freeUsed)
			.paidCredit(-paidUsed)
			.amount(-amount)
			.balanceAfter(balanceAfter)
			.description(description)
			.build();
	}

	public static Credit chargePaidCredit(
		User user,
		Payment payment,
		int amount,
		int balanceAfter
	) {
		return Credit.builder()
			.user(user)
			.payment(payment)
			.transactionType(TransactionType.CHARGE)
			.amount(amount)
			.freeCredit(0)
			.paidCredit(amount)
			.balanceAfter(balanceAfter)
			.description("유료 크레딧 충전")
			.build();
	}
}
