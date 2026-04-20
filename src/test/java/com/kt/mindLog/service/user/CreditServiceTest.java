package com.kt.mindLog.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kt.mindLog.domain.credit.Credit;
import com.kt.mindLog.domain.credit.ProductType;
import com.kt.mindLog.domain.credit.TransactionType;
import com.kt.mindLog.domain.payment.Payment;
import com.kt.mindLog.domain.report.ReportType;
import com.kt.mindLog.domain.user.LoginType;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.credit.CreditProductResponse;
import com.kt.mindLog.dto.credit.UserCreditResponse;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.repository.credit.CreditRepository;
import com.kt.mindLog.service.credit.CreditService;
import com.kt.mindLog.util.fixture.UserFixture;

@ExtendWith(MockitoExtension.class)
class CreditServiceTest {

	@InjectMocks
	private CreditService creditService;

	@Mock
	private CreditRepository creditRepository;

	@Mock
	private UserRepository userRepository;

	private static final String DEFAULT_PROFILE_URL = "https://s3.example.com/default-profile.png";

	private UUID userId;
	private User user;

	@BeforeEach
	void setUp() {
		user = UserFixture.buildActiveUser("test@kakao.com", LoginType.KAKAO, DEFAULT_PROFILE_URL);
		userId = user.getId();
	}

	// ────────────────────────────────────────────────────────────────────────────
	// 픽스처 헬퍼
	// ────────────────────────────────────────────────────────────────────────────

	private Credit buildFreeCredit(User user, TransactionType type, int amount, int balanceAfter) {
		return Credit.earnFreeCredit(user, type, amount, balanceAfter, 0, "테스트");
	}

	private Credit buildPaidCredit(User user, Payment payment, int amount, int balanceAfter) {
		return Credit.chargePaidCredit(user, payment, amount, balanceAfter);
	}

	// ────────────────────────────────────────────────────────────────────────────
	// earnSignupBonus()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("earnSignupBonus()")
	class EarnSignupBonus {

		@Test
		@DisplayName("회원가입 보너스 150 크레딧이 적립된다")
		void earnSignupBonus_savesCredit() {
			// given
			given(creditRepository.save(any(Credit.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			creditService.earnSignupBonus(user);

			// then
			then(creditRepository).should().save(argThat(credit ->
				credit.getTransactionType() == TransactionType.SIGNUP_BONUS &&
					credit.getAmount() == 150 &&
					credit.getFreeCredit() == 150 &&
					credit.getPaidCredit() == 0
			));
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// earnAttendanceBonus()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("earnAttendanceBonus()")
	class EarnAttendanceBonus {

		@Test
		@DisplayName("당일 출석 체크가 없으면 3 크레딧이 적립된다")
		void notCheckedToday_savesAttendanceBonus() {
			// given
			given(creditRepository.existsByUserIdAndTransactionTypeAndCreatedAtBetween(
				eq(user.getId()), eq(TransactionType.ATTENDANCE_CHECK), any(), any()))
				.willReturn(false);
			given(creditRepository.sumFreeCreditByUserId(user.getId())).willReturn(10);
			given(creditRepository.sumPaidCreditByUserId(user.getId())).willReturn(0);
			given(creditRepository.save(any(Credit.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			creditService.earnAttendanceBonus(user);

			// then
			then(creditRepository).should().save(argThat(credit ->
				credit.getTransactionType() == TransactionType.ATTENDANCE_CHECK &&
					credit.getAmount() == 3 &&
					credit.getFreeCredit() == 3 &&
					credit.getBalanceAfter() == 13
			));
		}

		@Test
		@DisplayName("당일 출석 체크가 이미 있으면 크레딧을 적립하지 않는다")
		void alreadyCheckedToday_doesNotSave() {
			// given
			given(creditRepository.existsByUserIdAndTransactionTypeAndCreatedAtBetween(
				eq(user.getId()), eq(TransactionType.ATTENDANCE_CHECK), any(), any()))
				.willReturn(true);

			// when
			creditService.earnAttendanceBonus(user);

			// then
			then(creditRepository).should(never()).save(any());
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// earnCreditForSession()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("earnCreditForSession()")
	class EarnCreditForSession {

		@Test
		@DisplayName("세션 저장 보너스 3 크레딧이 적립된다")
		void earnCreditForSession_savesCredit() {
			// given
			UUID sessionId = UUID.randomUUID();
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
			given(creditRepository.sumFreeCreditByUserId(userId)).willReturn(10);
			given(creditRepository.sumPaidCreditByUserId(userId)).willReturn(5);
			given(creditRepository.save(any(Credit.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			creditService.earnCreditForSession(userId, sessionId);

			// then
			then(creditRepository).should().save(argThat(credit ->
				credit.getTransactionType() == TransactionType.SAVE_SESSION &&
					credit.getAmount() == 3 &&
					credit.getFreeCredit() == 3 &&
					credit.getBalanceAfter() == 18 // (10+3) + 5
			));
		}

		@Test
		@DisplayName("존재하지 않는 userId이면 CustomException(NOT_FOUND_USER)을 던진다")
		void notFoundUser_throwsException() {
			// given
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER))
				.willThrow(new CustomException(ErrorCode.NOT_FOUND_USER));

			// when & then
			assertThatThrownBy(() -> creditService.earnCreditForSession(userId, UUID.randomUUID()))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.NOT_FOUND_USER);
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// useCreditForExtraSession()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("useCreditForExtraSession()")
	class UseCreditForExtraSession {

		@Test
		@DisplayName("무료 크레딧만으로 70 차감된다")
		void onlyFreeCredit_deductsCorrectly() {
			// given
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
			given(creditRepository.sumFreeCreditByUserId(userId)).willReturn(100);
			given(creditRepository.sumPaidCreditByUserId(userId)).willReturn(0);
			given(creditRepository.save(any(Credit.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			creditService.useCreditForExtraSession(userId);

			// then
			then(creditRepository).should().save(argThat(credit ->
				credit.getTransactionType() == TransactionType.EXTRA_SESSION &&
					credit.getAmount() == -70 &&
					credit.getFreeCredit() == -70 &&
					credit.getPaidCredit() == 0 &&
					credit.getBalanceAfter() == 30 // 100 - 70
			));
		}

		@Test
		@DisplayName("무료 크레딧이 부족하면 유료 크레딧으로 보충 차감된다")
		void freeNotEnough_deductsPaidCreditToo() {
			// given
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
			given(creditRepository.sumFreeCreditByUserId(userId)).willReturn(30);
			given(creditRepository.sumPaidCreditByUserId(userId)).willReturn(50);
			given(creditRepository.save(any(Credit.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			creditService.useCreditForExtraSession(userId);

			// then
			then(creditRepository).should().save(argThat(credit ->
				credit.getTransactionType() == TransactionType.EXTRA_SESSION &&
					credit.getFreeCredit() == -30 &&  // 무료 전액 사용
					credit.getPaidCredit() == -40 &&  // 부족분 유료로
					credit.getBalanceAfter() == 10    // (30-30) + (50-40)
			));
		}

		@Test
		@DisplayName("총 크레딧이 70 미만이면 CustomException(INSUFFICIENT_CREDIT)을 던진다")
		void insufficientCredit_throwsException() {
			// given
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
			given(creditRepository.sumFreeCreditByUserId(userId)).willReturn(30);
			given(creditRepository.sumPaidCreditByUserId(userId)).willReturn(20);

			// when & then
			assertThatThrownBy(() -> creditService.useCreditForExtraSession(userId))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.INSUFFICIENT_CREDIT);

			then(creditRepository).should(never()).save(any());
		}

		@Test
		@DisplayName("존재하지 않는 userId이면 CustomException(NOT_FOUND_USER)을 던진다")
		void notFoundUser_throwsException() {
			// given
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER))
				.willThrow(new CustomException(ErrorCode.NOT_FOUND_USER));

			// when & then
			assertThatThrownBy(() -> creditService.useCreditForExtraSession(userId))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.NOT_FOUND_USER);
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// getCredits()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("getCredits()")
	class GetCredits {

		@Test
		@DisplayName("총 크레딧과 거래 내역 목록을 반환한다")
		void returnsUserCreditResponse() {
			// given
			Credit credit = buildFreeCredit(user, TransactionType.SIGNUP_BONUS, 150, 150);
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
			given(creditRepository.sumFreeCreditByUserId(userId)).willReturn(150);
			given(creditRepository.sumPaidCreditByUserId(userId)).willReturn(0);
			given(creditRepository.findAllByUserId(userId)).willReturn(List.of(credit));

			// when
			UserCreditResponse response = creditService.getCredits(userId);

			// then
			assertThat(response).isNotNull();
			assertThat(response.totalCredit()).isEqualTo(150);
			assertThat(response.transactions()).hasSize(1);
		}

		@Test
		@DisplayName("거래 내역이 없으면 빈 리스트를 반환한다")
		void noTransactions_returnsEmptyList() {
			// given
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
			given(creditRepository.sumFreeCreditByUserId(userId)).willReturn(0);
			given(creditRepository.sumPaidCreditByUserId(userId)).willReturn(0);
			given(creditRepository.findAllByUserId(userId)).willReturn(List.of());

			// when
			UserCreditResponse response = creditService.getCredits(userId);

			// then
			assertThat(response.totalCredit()).isZero();
			assertThat(response.transactions()).isEmpty();
		}

		@Test
		@DisplayName("존재하지 않는 userId이면 CustomException(NOT_FOUND_USER)을 던진다")
		void notFoundUser_throwsException() {
			// given
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER))
				.willThrow(new CustomException(ErrorCode.NOT_FOUND_USER));

			// when & then
			assertThatThrownBy(() -> creditService.getCredits(userId))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.NOT_FOUND_USER);
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// getCreditProducts()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("getCreditProducts()")
	class GetCreditProducts {

		@Test
		@DisplayName("소형/중형/대형 3개의 상품 목록을 반환한다")
		void returnsThreeProducts() {
			// when
			List<CreditProductResponse> products = creditService.getCreditProducts();

			// then
			assertThat(products).hasSize(3);
			assertThat(products).extracting(CreditProductResponse::name)
				.containsExactly("소형", "중형", "대형");
		}

		@Test
		@DisplayName("각 상품의 크레딧과 가격이 올바르다")
		void productDetails_areCorrect() {
			// when
			List<CreditProductResponse> products = creditService.getCreditProducts();

			// then
			assertThat(products.get(0).creditAmount()).isEqualTo(200);
			assertThat(products.get(0).price()).isEqualTo(2200);
			assertThat(products.get(1).creditAmount()).isEqualTo(500);
			assertThat(products.get(1).price()).isEqualTo(4900);
			assertThat(products.get(2).creditAmount()).isEqualTo(1200);
			assertThat(products.get(2).price()).isEqualTo(10900);
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// validateRefundable()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("validateRefundable()")
	class ValidateRefundable {

		@Test
		@DisplayName("유료 크레딧이 사용되지 않았으면 예외를 던지지 않는다")
		void notUsed_doesNotThrow() {
			// given
			UUID paymentId = UUID.randomUUID();
			Credit credit = Credit.chargePaidCredit(user, null, 200, 200);
			// remainingPaidCredit == paidCredit → 사용 안 됨
			given(creditRepository.findByPaymentId(paymentId)).willReturn(List.of(credit));

			// when & then
			assertThatCode(() -> creditService.validateRefundable(paymentId))
				.doesNotThrowAnyException();
		}

		@Test
		@DisplayName("유료 크레딧이 일부라도 사용됐으면 CustomException(ALREADY_USED_CREDIT)을 던진다")
		void partiallyUsed_throwsException() {
			// given
			UUID paymentId = UUID.randomUUID();
			Credit credit = Credit.chargePaidCredit(user, null, 200, 200);
			credit.deductRemainingPaidCredit(50); // 50 사용

			given(creditRepository.findByPaymentId(paymentId)).willReturn(List.of(credit));

			// when & then
			assertThatThrownBy(() -> creditService.validateRefundable(paymentId))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.ALREADY_USED_CREDIT);
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// revokePaidCredits()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("revokePaidCredits()")
	class RevokePaidCredits {

		@Test
		@DisplayName("크레딧이 없으면 아무것도 저장하지 않는다")
		void noCredits_doesNotSave() {
			// given
			UUID paymentId = UUID.randomUUID();
			given(creditRepository.findByPaymentId(paymentId)).willReturn(List.of());

			// when
			creditService.revokePaidCredits(paymentId);

			// then
			then(creditRepository).should(never()).save(any());
		}

		@Test
		@DisplayName("사용되지 않은 유료 크레딧은 환불 크레딧으로 저장된다")
		void unusedCredit_savesRefundCredit() {
			// given
			UUID paymentId = UUID.randomUUID();
			Credit credit = Credit.chargePaidCredit(user, null, 200, 200);
			// remainingPaidCredit == paidCredit == 200 → 미사용

			given(creditRepository.findByPaymentId(paymentId)).willReturn(List.of(credit));
			given(creditRepository.sumPaidCreditByUserId(user.getId())).willReturn(200);
			given(creditRepository.sumFreeCreditByUserId(user.getId())).willReturn(0);
			given(creditRepository.save(any(Credit.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			creditService.revokePaidCredits(paymentId);

			// then
			then(creditRepository).should().save(argThat(refund ->
				refund.getTransactionType() == TransactionType.REFUND &&
					refund.getAmount() == -200 &&
					refund.getPaidCredit() == -200
			));
		}

		@Test
		@DisplayName("유료 크레딧이 사용됐으면 CustomException(ALREADY_USED_CREDIT)을 던진다")
		void usedCredit_throwsException() {
			// given
			UUID paymentId = UUID.randomUUID();
			Credit credit = Credit.chargePaidCredit(user, null, 200, 200);
			credit.deductRemainingPaidCredit(50); // 50 사용

			given(creditRepository.findByPaymentId(paymentId)).willReturn(List.of(credit));
			given(creditRepository.sumPaidCreditByUserId(user.getId())).willReturn(200);
			given(creditRepository.sumFreeCreditByUserId(user.getId())).willReturn(0);

			// when & then
			assertThatThrownBy(() -> creditService.revokePaidCredits(paymentId))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.ALREADY_USED_CREDIT);
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// chargePaidCredit()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("chargePaidCredit()")
	class ChargePaidCredit {

		@Test
		@DisplayName("결제 상품 크레딧만큼 유료 크레딧이 충전된다")
		void chargesPaidCreditByProductAmount() {
			// given
			Payment payment = mock(Payment.class);
			given(payment.getProductType()).willReturn(ProductType.SMALL); // 200 크레딧
			given(creditRepository.sumPaidCreditByUserId(user.getId())).willReturn(0);
			given(creditRepository.sumFreeCreditByUserId(user.getId())).willReturn(50);
			given(creditRepository.save(any(Credit.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			creditService.chargePaidCredit(user, payment);

			// then
			then(creditRepository).should().save(argThat(credit ->
				credit.getTransactionType() == TransactionType.CHARGE &&
					credit.getPaidCredit() == 200 &&
					credit.getFreeCredit() == 0 &&
					credit.getBalanceAfter() == 250 // 50 + 0 + 200
			));
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// validateCreditForReport()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("validateCreditForReport()")
	class ValidateCreditForReport {

		@Test
		@DisplayName("크레딧이 충분하면 예외를 던지지 않는다")
		void sufficientCredit_doesNotThrow() {
			// given — WEEKLY = 150 크레딧 필요 (ReportType 기준)
			given(creditRepository.sumFreeCreditByUserId(userId)).willReturn(150);
			given(creditRepository.sumPaidCreditByUserId(userId)).willReturn(0);

			// when & then
			assertThatCode(() -> creditService.validateCreditForReport(userId, ReportType.WEEKLY))
				.doesNotThrowAnyException();
		}

		@Test
		@DisplayName("크레딧이 부족하면 CustomException(INSUFFICIENT_CREDIT)을 던진다")
		void insufficientCredit_throwsException() {
			// given
			given(creditRepository.sumFreeCreditByUserId(userId)).willReturn(50);
			given(creditRepository.sumPaidCreditByUserId(userId)).willReturn(0);

			// when & then
			assertThatThrownBy(() -> creditService.validateCreditForReport(userId, ReportType.WEEKLY))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.INSUFFICIENT_CREDIT);
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// useCreditForReport()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("useCreditForReport()")
	class UseCreditForReport {

		@Test
		@DisplayName("무료 크레딧만으로 리포트 크레딧이 차감된다")
		void onlyFreeCredit_deductsCorrectly() {
			// given — WEEKLY = 150
			given(creditRepository.sumFreeCreditByUserId(userId)).willReturn(200);
			given(creditRepository.sumPaidCreditByUserId(userId)).willReturn(0);
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
			given(creditRepository.save(any(Credit.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			creditService.useCreditForReport(userId, ReportType.WEEKLY);

			// then
			then(creditRepository).should().save(argThat(credit ->
				credit.getFreeCredit() == -150 &&
					credit.getPaidCredit() == 0 &&
					credit.getBalanceAfter() == 50 // 200 - 150
			));
		}

		@Test
		@DisplayName("무료 크레딧이 부족하면 유료 크레딧으로 보충 차감된다")
		void freeNotEnough_deductsPaidCreditToo() {
			// given — WEEKLY = 150, 무료 50 + 유료 200
			given(creditRepository.sumFreeCreditByUserId(userId)).willReturn(50);
			given(creditRepository.sumPaidCreditByUserId(userId)).willReturn(200);
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
			given(creditRepository.save(any(Credit.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			creditService.useCreditForReport(userId, ReportType.WEEKLY);

			// then
			then(creditRepository).should().save(argThat(credit ->
				credit.getFreeCredit() == -50 &&   // 무료 전액 사용
					credit.getPaidCredit() == -100 &&  // 부족분 유료로
					credit.getBalanceAfter() == 100    // (50-50) + (200-100)
			));
		}

		@Test
		@DisplayName("존재하지 않는 userId이면 CustomException(NOT_FOUND_USER)을 던진다")
		void notFoundUser_throwsException() {
			// given
			given(creditRepository.sumFreeCreditByUserId(userId)).willReturn(200);
			given(creditRepository.sumPaidCreditByUserId(userId)).willReturn(0);
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER))
				.willThrow(new CustomException(ErrorCode.NOT_FOUND_USER));

			// when & then
			assertThatThrownBy(() -> creditService.useCreditForReport(userId, ReportType.WEEKLY))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.NOT_FOUND_USER);
		}
	}
}