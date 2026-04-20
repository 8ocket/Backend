package com.kt.mindLog.service.credit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.credit.Credit;
import com.kt.mindLog.domain.credit.TransactionType;
import com.kt.mindLog.domain.payment.Payment;
import com.kt.mindLog.domain.report.ReportType;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.credit.CreditProductResponse;
import com.kt.mindLog.dto.credit.UserCreditResponse;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.repository.credit.CreditRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditService {
	private final CreditRepository creditRepository;
	private final UserRepository userRepository;

	private static final int SIGNUP_BONUS = 150;
	private static final int ATTENDANCE_BONUS = 3;
	private static final int SAVE_SESSION = 3;
	private static final int EXTRA_SESSION = 70;

	@Transactional
	public void earnSignupBonus(User user) {
		Credit credit = Credit.earnFreeCredit(
			user,
			TransactionType.SIGNUP_BONUS,
			SIGNUP_BONUS,
			SIGNUP_BONUS,
			0,
			"회원가입 보너스 지급"
		);

		creditRepository.save(credit);
	}

	@Transactional
	public void earnAttendanceBonus(User user) {
		LocalDate today = LocalDate.now();
		LocalDateTime startOfToday = today.atStartOfDay();
		LocalDateTime endOfToday = today.atTime(LocalTime.MAX);

		boolean alreadyChecked = creditRepository
			.existsByUserIdAndTransactionTypeAndCreatedAtBetween(
				user.getId(),
				TransactionType.ATTENDANCE_CHECK,
				startOfToday,
				endOfToday
			);

		if (alreadyChecked) {
			log.info("이미 출석 체크 완료 userId={}", user.getId());
			return;
		}

		int currentFree = creditRepository.sumFreeCreditByUserId(user.getId());
		int currentPaid = creditRepository.sumPaidCreditByUserId(user.getId());

		int newFreeCredit = currentFree + ATTENDANCE_BONUS;
		int balanceAfter = newFreeCredit + currentPaid;

		Credit credit = Credit.earnFreeCredit(
			user,
			TransactionType.ATTENDANCE_CHECK,
			ATTENDANCE_BONUS,
			balanceAfter,
			currentPaid,
			"출석체크 보너스 지급"
		);

		creditRepository.save(credit);
	}

	@Transactional
	public void earnCreditForSession(UUID userId, UUID sessionId) {
		User user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		// 현재 크레딧 조회
		int currentFree = creditRepository.sumFreeCreditByUserId(userId);
		int currentPaid = creditRepository.sumPaidCreditByUserId(userId);

		// 지급할 크레딧 계산
		int newFreeCredit = currentFree + SAVE_SESSION;
		int balanceAfter = newFreeCredit + currentPaid;

		Credit credit = Credit.earnFreeCredit(
			user,
			TransactionType.SAVE_SESSION,
			SAVE_SESSION,
			balanceAfter,
			currentPaid,
			"세션 저장 보너스 지급"
		);

		creditRepository.save(credit);
	}

	@Transactional
	public void useCreditForExtraSession(UUID userId) {
		User user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		// 현재 크레딧 조회
		int currentFree = creditRepository.sumFreeCreditByUserId(userId);
		int currentPaid = creditRepository.sumPaidCreditByUserId(userId);

		int total = currentFree + currentPaid;

		// 크레딧 체크
		Preconditions.validate(total >= EXTRA_SESSION, ErrorCode.INSUFFICIENT_CREDIT);

		// 무료 크레딧 최대 사용
		int freeUsed = Math.min(currentFree, EXTRA_SESSION);
		int paidUsed = EXTRA_SESSION - freeUsed;

		// 크레딧 차감 후 잔액 계산
		int balanceAfter = (currentFree - freeUsed) + (currentPaid - paidUsed);

		Credit credit = Credit.useCredit(
			user,
			TransactionType.EXTRA_SESSION,
			EXTRA_SESSION,
			freeUsed,
			paidUsed,
			balanceAfter,
			currentPaid - paidUsed,
			"추가 상담 크레딧 차감"
		);

		creditRepository.save(credit);
	}

	public UserCreditResponse getCredits(UUID userId) {
		User user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		int currentFree = creditRepository.sumFreeCreditByUserId(userId);
		int currentPaid = creditRepository.sumPaidCreditByUserId(userId);
		int totalCredit = currentFree + currentPaid;

		List<UserCreditResponse.CreditTransaction> transactions = creditRepository.findAllByUserId(userId).stream()
			.map(credit -> UserCreditResponse.CreditTransaction.of(
				credit.getTransactionType(),
				credit.getAmount(),
				credit.getCreatedAt()
			))
			.toList();

		return UserCreditResponse.of(totalCredit, transactions);
	}

	public List<CreditProductResponse> getCreditProducts() {
		return List.of(
			CreditProductResponse.of("소형", 200, 2200),
			CreditProductResponse.of("중형", 500, 4900),
			CreditProductResponse.of("대형", 1200, 10900)
		);
	}

	public void validateRefundable(UUID paymentId) {
		List<Credit> credits = creditRepository.findByPaymentId(paymentId);

		for (Credit credit : credits) {
			boolean isUsed = credit.getRemainingPaidCredit() < credit.getPaidCredit();

			Preconditions.validate(!isUsed, ErrorCode.ALREADY_USED_CREDIT);
		}
	}

	public void revokePaidCredits(UUID paymentId) {
		List<Credit> credits = creditRepository.findByPaymentId(paymentId);

		if (credits.isEmpty()) return;

		UUID userId = credits.get(0).getUser().getId();

		int currentPaid = creditRepository.sumPaidCreditByUserId(userId);
		int currentFree = creditRepository.sumFreeCreditByUserId(userId);

		// 총 환불액 먼저 계산
		int totalRefund = credits.stream()
			.mapToInt(Credit::getRemainingPaidCredit)
			.sum();

		int balanceAfter = currentFree + currentPaid - totalRefund;

		for (Credit credit : credits) {
			int usedPaid = credit.getPaidCredit() - credit.getRemainingPaidCredit();
			Preconditions.validate(usedPaid == 0, ErrorCode.ALREADY_USED_CREDIT);

			int refundAmount = credit.getRemainingPaidCredit();

			Credit refund = Credit.builder()
				.user(credit.getUser())
				.payment(credit.getPayment())
				.transactionType(TransactionType.REFUND)
				.amount(-refundAmount)
				.paidCredit(-refundAmount)
				.freeCredit(0)
				.remainingPaidCredit(0)
				.balanceAfter(balanceAfter)
				.description("유료 크레딧 환불")
				.build();

			creditRepository.save(refund);
		}
	}

	@Transactional
	public void chargePaidCredit(User user, Payment payment) {
		int creditAmount = payment.getProductType().getCredit();

		int currentPaid = creditRepository.sumPaidCreditByUserId(user.getId());
		int currentFree = creditRepository.sumFreeCreditByUserId(user.getId());

		int balanceAfter = currentPaid + currentFree + creditAmount;

		Credit charge = Credit.chargePaidCredit(
			user,
			payment,
			creditAmount,
			balanceAfter
		);

		creditRepository.save(charge);
	}

	public void validateCreditForReport(UUID userId, ReportType type) {
		int currentFree = creditRepository.sumFreeCreditByUserId(userId);
		int currentPaid = creditRepository.sumPaidCreditByUserId(userId);
		int total = currentFree + currentPaid;

		Preconditions.validate(total >= type.getAmount(), ErrorCode.INSUFFICIENT_CREDIT);
	}

	@Transactional
	public void useCreditForReport(UUID userId, ReportType type) {
		int amount = type.getAmount();

		// 사용자 크레딧 조회
		int currentFree = creditRepository.sumFreeCreditByUserId(userId);
		int currentPaid = creditRepository.sumPaidCreditByUserId(userId);

		// 무료 크레딧 먼저 차감
		int freeUsed = Math.min(currentFree, amount);
		int paidUsed = amount - freeUsed;
		int remainingPaidCredit = currentPaid - paidUsed;

		// 차감 후 총 잔액
		int balanceAfter = (currentFree - freeUsed) + remainingPaidCredit;

		User user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		Credit credit = Credit.useCredit(
			user,
			type.getTransactionType(),
			amount,
			freeUsed,
			paidUsed,
			balanceAfter,
			remainingPaidCredit,
			"AI 리포트 생성 차감"
		);

		creditRepository.save(credit);
	}


}
