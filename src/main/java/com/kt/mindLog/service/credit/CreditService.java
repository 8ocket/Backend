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
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.credit.CreditProductResponse;
import com.kt.mindLog.dto.credit.UserCreditResponse;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.repository.credit.CreditRepository;
import com.kt.mindLog.repository.session.SessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditService {
	private final CreditRepository creditRepository;
	private final UserRepository userRepository;
	private final SessionRepository sessionRepository;

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

		Preconditions.validate(!alreadyChecked, ErrorCode.ATTENDANCE_ALREADY_COMPLETED);

		int currentFree = creditRepository.sumFreeCreditByUserId(user.getId());
		int currentPaid = creditRepository.sumPaidCreditByUserId(user.getId());

		int newFreeCredit = currentFree + ATTENDANCE_BONUS;
		int balanceAfter = newFreeCredit + currentPaid;

		Credit credit = Credit.earnFreeCredit(
			user,
			TransactionType.ATTENDANCE_CHECK,
			ATTENDANCE_BONUS,
			balanceAfter,
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
		Preconditions.validate(!(total < EXTRA_SESSION), ErrorCode.INSUFFICIENT_CREDIT);

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
			"추가 상담 크레딧 차감"
		);

		creditRepository.save(credit);
	}

	public UserCreditResponse getCredits(UUID userId) {
		User user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		int currentFree = creditRepository.sumFreeCreditByUserId(userId);
		int currentPaid = creditRepository.sumPaidCreditByUserId(userId);

		int totalCredit = currentFree + currentPaid;

		return UserCreditResponse.from(totalCredit);
	}

	public List<CreditProductResponse> getCreditProducts() {
		return List.of(
			CreditProductResponse.of("소형", 200, 2200),
			CreditProductResponse.of("중형", 500, 4900),
			CreditProductResponse.of("대형", 1200, 10900)
		);
	}
}
