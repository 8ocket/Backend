package com.kt.mindLog.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.kt.mindLog.domain.user.Gender;
import com.kt.mindLog.domain.user.LoginType;
import com.kt.mindLog.domain.user.Occupation;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.util.fixture.UserFixture;
import com.kt.mindLog.dto.user.request.UserCreateRequest;
import com.kt.mindLog.dto.user.request.UserUpdateRequest;
import com.kt.mindLog.dto.user.response.LoginResponse;
import com.kt.mindLog.dto.user.response.UserProfileResponse;
import com.kt.mindLog.dto.user.response.UserUpdateProfileResponse;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.service.attendance.AttendanceService;
import com.kt.mindLog.service.credit.CreditService;
import com.kt.mindLog.service.s3.S3Path;
import com.kt.mindLog.service.s3.S3Service;

@ExtendWith(MockitoExtension.class)
class UserServiceTest{

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtService jwtService;

	@Mock
	private S3Service s3Service;

	@Mock
	private CreditService creditService;

	@Mock
	private AttendanceService attendanceService;

	private static final String DEFAULT_PROFILE_URL = "https://s3.example.com/default-profile.png";

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(userService, "defaultProfile", DEFAULT_PROFILE_URL);
	}

	// ────────────────────────────────────────────────────────────────────────────
	// 테스트 픽스처 헬퍼
	// ────────────────────────────────────────────────────────────────────────────

	/** 최초 OAuth 인증만 된 비활성 사용자(isActive=false) */
	private User inactiveUser(String email, LoginType loginType) {
		return UserFixture.buildInactiveUser(email, loginType);
	}

	/** 회원가입 완료된 활성 사용자(isActive=true) */
	private User activeUser(String email, LoginType loginType) {
		return UserFixture.buildActiveUser(email, loginType, DEFAULT_PROFILE_URL);
	}

	private LoginResponse loginResponse(boolean isNewUser) {
		return LoginResponse.builder()
			.accessToken("access-token")
			.refreshToken("refresh-token")
			.isNewUser(isNewUser)
			.build();
	}

	// ────────────────────────────────────────────────────────────────────────────
	// login()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("login()")
	class Login {

		@Test
		@DisplayName("기존 정상 회원 로그인 시 isNewUser=false 로 jwt 토큰을 반환한다")
		void existingActiveUser_returnsIsNewUserFalse() {
			// given
			User user = activeUser("test@kakao.com", LoginType.KAKAO);
			given(userRepository.findByEmailAndLoginType("test@kakao.com", LoginType.KAKAO))
				.willReturn(Optional.of(user));
			given(jwtService.createJwtTokens(user, false)).willReturn(loginResponse(false));

			// when
			LoginResponse response = userService.login("test@kakao.com", LoginType.KAKAO);

			// then
			assertThat(response.isNewUser()).isFalse();
			then(creditService).should().earnAttendanceBonus(user);
			then(attendanceService).should().saveAttendance(any(UUID.class));
		}

		@Test
		@DisplayName("가입 미완료 회원 로그인 시 isNewUser=true 로 토큰을 반환한다")
		void existingInactiveUser_returnsIsNewUserTrue() {
			// given
			User user = inactiveUser("new@kakao.com", LoginType.KAKAO);
			given(userRepository.findByEmailAndLoginType("new@kakao.com", LoginType.KAKAO))
				.willReturn(Optional.of(user));
			given(jwtService.createJwtTokens(user, true)).willReturn(loginResponse(true));

			// when
			LoginResponse response = userService.login("new@kakao.com", LoginType.KAKAO);

			// then
			assertThat(response.isNewUser()).isTrue();
		}

		@Test
		@DisplayName("최초 소셜 로그인 시 User를 저장하고 isNewUser=true 로 토큰을 반환한다")
		void brandNewUser_savesUserAndReturnsIsNewUserTrue() {
			// given
			given(userRepository.findByEmailAndLoginType("brand@google.com", LoginType.GOOGLE))
				.willReturn(Optional.empty());
			given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));
			given(jwtService.createJwtTokens(any(User.class), eq(true))).willReturn(loginResponse(true));

			// when
			LoginResponse response = userService.login("brand@google.com", LoginType.GOOGLE);

			// then
			assertThat(response.isNewUser()).isTrue();
			then(userRepository).should().save(any(User.class));
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// createUserInfo()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("createUserInfo()")
	class CreateUserInfo {

		private final UUID userId = UUID.randomUUID();
		private final UserCreateRequest request =
			new UserCreateRequest("테스터", Occupation.STUDENT, 25, Gender.MALE);

		@Test
		@DisplayName("프로필 이미지 없이 가입하면 기본 프로필 이미지 URL이 저장된다")
		void emptyProfile_usesDefaultImage() {
			// given
			User user = inactiveUser("test@kakao.com", LoginType.KAKAO);
			MockMultipartFile emptyFile = new MockMultipartFile("profile", new byte[0]);

			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
			given(userRepository.save(user)).willReturn(user);

			// when
			userService.createUserInfo(userId, emptyFile, request);

			// then
			assertThat(user.getProfileImageUrl()).isEqualTo(DEFAULT_PROFILE_URL);
			assertThat(user.isActive()).isTrue();
			then(s3Service).should(never()).uploadImage(any(), any());
			then(creditService).should().earnSignupBonus(user);
		}

		@Test
		@DisplayName("프로필 이미지가 있으면 S3에 업로드된 이미지 URL이 저장된다")
		void withProfile_uploadsToS3() {
			// given
			User user = inactiveUser("test@kakao.com", LoginType.KAKAO);
			MockMultipartFile file = new MockMultipartFile(
				"profile", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});
			String uploadedUrl = "https://s3.example.com/profiles/photo.jpg";

			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
			given(s3Service.uploadImage(file, S3Path.PROFILE)).willReturn(uploadedUrl);

			// when
			userService.createUserInfo(userId, file, request);

			// then
			assertThat(user.getProfileImageUrl()).isEqualTo(uploadedUrl);
		}

		@Test
		@DisplayName("존재하지 않는 userId 이면 CustomException(NOT_FOUND_USER)을 던진다")
		void notFoundUser_throwsException() {
			// given
			MockMultipartFile emptyFile = new MockMultipartFile("profile", new byte[0]);
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER))
				.willThrow(new CustomException(ErrorCode.NOT_FOUND_USER));

			// when & then
			assertThatThrownBy(() -> userService.createUserInfo(userId, emptyFile, request))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.NOT_FOUND_USER);
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// updateProfile()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("updateProfile()")
	class UpdateProfile {

		private final UUID userId = UUID.randomUUID();

		@Test
		@DisplayName("닉네임 변경 시 닉네임 변경 횟수가 1 증가한다")
		void nicknameChange_incrementsCount() {
			// given
			User user = activeUser("test@kakao.com", LoginType.KAKAO);
			UserUpdateRequest request = new UserUpdateRequest("새닉네임", null, null, null);

			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);

			// when
			userService.updateProfile(userId, null, request);

			// then
			assertThat(user.getNicknameChangeCount()).isEqualTo(1);
		}

		@Test
		@DisplayName("닉네임 변경 횟수가 3회 이상이면 CustomException(INVALID_NICKNAME_CHANGE)을 던진다")
		void nicknameChangeLimitExceeded_throwsException() {
			// given
			User user = activeUser("test@kakao.com", LoginType.KAKAO);
			// 이미 3회 변경한 상태 설정
			user.updateNicknameCount();
			user.updateNicknameCount();
			user.updateNicknameCount();

			UserUpdateRequest request = new UserUpdateRequest("또다른닉네임", null, null, null);
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);

			// when & then
			assertThatThrownBy(() -> userService.updateProfile(userId, null, request))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.INVALID_NICKNAME_CHANGE);
		}

		@Test
		@DisplayName("현재 닉네임과 동일한 닉네임으로 변경하면 CustomException(SAME_NICKNAME_NOT_ALLOWED)을 던진다")
		void sameNickname_throwsException() {
			// given
			User user = activeUser("test@kakao.com", LoginType.KAKAO); // 닉네임: "테스터"
			UserUpdateRequest request = new UserUpdateRequest("테스터", null, null, null);

			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);

			// when & then
			assertThatThrownBy(() -> userService.updateProfile(userId, null, request))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.SAME_NICKNAME_NOT_ALLOWED);
		}

		@Test
		@DisplayName("닉네임 없이 직업만 변경하면 닉네임 변경 횟수는 그대로다")
		void occupationOnlyChange_doesNotIncrementNicknameCount() {
			// given
			User user = activeUser("test@kakao.com", LoginType.KAKAO);
			UserUpdateRequest request = new UserUpdateRequest(null, Occupation.EMPLOYEE, null, null);

			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);

			// when
			UserUpdateProfileResponse response = userService.updateProfile(userId, null, request);

			// then
			assertThat(user.getNicknameChangeCount()).isZero();
			assertThat(response).isNotNull();
		}

		@Test
		@DisplayName("프로필 이미지 변경 시 S3에 업로드된 이미지 URL로 업데이트된다")
		void profileImageChange_uploadsToS3() {
			// given
			User user = activeUser("test@kakao.com", LoginType.KAKAO);
			MockMultipartFile newImage = new MockMultipartFile(
				"profile", "new.jpg", "image/jpeg", new byte[]{1, 2, 3});
			String newUrl = "https://s3.example.com/profiles/new.jpg";

			UserUpdateRequest request = new UserUpdateRequest(null, null, null, null);
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
			given(s3Service.uploadImage(newImage, S3Path.PROFILE)).willReturn(newUrl);

			// when
			userService.updateProfile(userId, newImage, request);

			// then
			assertThat(user.getProfileImageUrl()).isEqualTo(newUrl);
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// getProfile()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("getProfile()")
	class GetProfile {

		@Test
		@DisplayName("존재하는 userId 이면 UserProfileResponse를 반환한다")
		void existingUser_returnsProfile() {
			// given
			UUID userId = UUID.randomUUID();
			User user = activeUser("test@kakao.com", LoginType.KAKAO);
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);

			// when
			UserProfileResponse response = userService.getProfile(userId);

			// then
			assertThat(response).isNotNull();
			assertThat(response.nickname()).isEqualTo("테스터");
		}

		@Test
		@DisplayName("존재하지 않는 userId 이면 CustomException(NOT_FOUND_USER)을 던진다")
		void notFoundUser_throwsException() {
			// given
			UUID userId = UUID.randomUUID();
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER))
				.willThrow(new CustomException(ErrorCode.NOT_FOUND_USER));

			// when & then
			assertThatThrownBy(() -> userService.getProfile(userId))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.NOT_FOUND_USER);
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// withdrawUser()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("withdrawUser()")
	class WithdrawUser {

		@Test
		@DisplayName("사용자 탈퇴 시 isActive=false 처리되며, 이메일이 마스킹된다")
		void activeUser_withdrawsSuccessfully() {
			// given
			UUID userId = UUID.randomUUID();
			User user = activeUser("test@kakao.com", LoginType.KAKAO);
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
			willDoNothing().given(jwtService).deleteToken(userId);

			// when
			userService.withdrawUser(userId);

			// then
			assertThat(user.isActive()).isFalse();
			assertThat(user.getEmail()).startsWith("withdrawn_");
			then(jwtService).should().deleteToken(userId);
		}

		@Test
		@DisplayName("이미 탈퇴한 사용자는 CustomException(INVALID_ACCESS)을 던진다")
		void alreadyWithdrawnUser_throwsException() {
			// given
			UUID userId = UUID.randomUUID();
			User user = inactiveUser("test@kakao.com", LoginType.KAKAO); // isActive=false
			given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);

			// when & then
			assertThatThrownBy(() -> userService.withdrawUser(userId))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.INVALID_ACCESS);

			then(jwtService).should(never()).deleteToken(any());
		}
	}
}
