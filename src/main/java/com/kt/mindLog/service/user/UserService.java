package com.kt.mindLog.service.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kt.mindLog.domain.user.LoginType;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.user.request.UserCreateRequest;
import com.kt.mindLog.dto.user.response.LoginResponse;
import com.kt.mindLog.dto.user.response.UserProfileResponse;
import com.kt.mindLog.dto.user.response.UserUpdateProfileResponse;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.common.support.Preconditions;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.service.attendance.AttendanceService;
import com.kt.mindLog.service.credit.CreditService;
import com.kt.mindLog.service.s3.S3Path;
import com.kt.mindLog.service.s3.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final S3Service s3Service;
	private final CreditService creditService;
	private final AttendanceService attendanceService;

	@Value("${default.image.profile}")
	private String defaultProfile;

	@Transactional
	public LoginResponse login(final String email, final LoginType loginType) {

		Optional<User> findUser = userRepository.findByEmailAndLoginType(email, loginType);

		if (findUser.isPresent()) {
			findUser.get().updateLastLoginAt();
			creditService.earnAttendanceBonus(findUser.get());
			attendanceService.saveAttendance(findUser.get().getId());
			return jwtService.createJwtTokens(findUser.get(), false);
		}

		User newUser = User.builder()
			.email(email)
			.loginType(loginType)
			.build();

		userRepository.save(newUser);

		return jwtService.createJwtTokens(newUser, true);
	}


	@Transactional
	public void createUserInfo(final UUID userId, final MultipartFile profile, final UserCreateRequest request) {

		User user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		String profileImageUrl = "";

		if (profile.isEmpty()) {
			profileImageUrl = defaultProfile;
		} else {
			profileImageUrl = s3Service.uploadImage(profile, S3Path.PROFILE);
		}

		user.updateUserInfo(
			request.nickname(),
			profileImageUrl,
			request.occupation(),
			request.age(),
			request.gender()
		);

		creditService.earnSignupBonus(user);

		userRepository.save(user);
		log.info("success to update user information");
	}

	@Transactional
	public UserUpdateProfileResponse updateProfile(final  UUID userId, final MultipartFile profile, final String nickname) {
		User user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		String newProfileImageUrl = user.getProfileImageUrl();
		String newNickname = user.getNickname();

		if (profile != null && !profile.isEmpty()) {
			newProfileImageUrl = s3Service.uploadImage(profile, S3Path.PROFILE);
		}

		if (nickname != null && !nickname.isBlank()) {
			Preconditions.validate(user.getNicknameChangeCount() < 3, ErrorCode.INVALID_NICKNAME_CHANGE);
			Preconditions.validate(!user.getNickname().equals(nickname), ErrorCode.SAME_NICKNAME_NOT_ALLOWED);

			newNickname = nickname;
			user.updateNicknameCount();
		}

		user.updateUserProfile(newProfileImageUrl, newNickname);
		return UserUpdateProfileResponse.updateProfile(userId, newProfileImageUrl, newNickname);
	}

	public UserProfileResponse getProfile(final UUID userId) {
		User user =  userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		return UserProfileResponse.from(user);
	}

	@Scheduled(cron = "0 0 0 1 * *")
	@Transactional
	public void resetNicknameChangeCount(){
		userRepository.resetNicknameChangeCount();
		log.info("reset nickname change count for all users");
	}
}
