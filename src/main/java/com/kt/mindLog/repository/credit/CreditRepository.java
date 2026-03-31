package com.kt.mindLog.repository.credit;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kt.mindLog.domain.credit.Credit;
import com.kt.mindLog.domain.credit.TransactionType;

public interface CreditRepository extends JpaRepository<Credit, UUID> {
	boolean existsByUserIdAndTransactionTypeAndCreatedAtBetween(UUID userId, TransactionType transactionType,
		LocalDateTime start, LocalDateTime end);

	// 무료 크레딧 합계 (null 대신 0 반환)
	@Query("SELECT COALESCE(SUM(c.freeCredit), 0) FROM Credit c WHERE c.user.id = :userId")
	int sumFreeCreditByUserId(UUID userId);

	// 유료 크레딧 합계 (null 대신 0 반환)
	@Query("SELECT COALESCE(SUM(c.paidCredit), 0) FROM Credit c WHERE c.user.id = :userId")
	int sumPaidCreditByUserId(UUID userId);


}
