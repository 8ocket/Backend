package com.kt.mindLog.repository.payment;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.payment.Payment;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
	default Payment findByIdOrThrow(UUID id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	Page<Payment> findByUserId(UUID userId, Pageable pageable);

	Optional<Payment> findByOrderId(String orderId);
}
