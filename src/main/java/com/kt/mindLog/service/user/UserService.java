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
import com.kt.mindLog.dto.user.request.UserUpdateRequest;
import com.kt.mindLog.dto.user.response.LoginResponse;
import com.kt.mindLog.dto.user.response.UserProfileResponse;
import com.kt.mindLog.dto.user.response.UserUpdateProfileResponse;
import com.kt.mindLog.global.common.exception.CustomException;
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

			return findUser.get().isActive() ? jwtService.createJwtTokens(findUser.get(), false)
				: jwtService.createJwtTokens(findUser.get(), true);
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

		if (profile.isEmpty() || profile == null) {
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
	public UserUpdateProfileResponse updateProfile(final  UUID userId, final MultipartFile profile, final
		UserUpdateRequest request) {
		User user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		String newProfileImageUrl = user.getProfileImageUrl();
		String newNickname = user.getNickname();

		var newOccupation = user.getOccupation();
		var newAge = user.getAge();
		var newGender = user.getGender();

		if (profile != null && !profile.isEmpty()) {
			newProfileImageUrl = s3Service.uploadImage(profile, S3Path.PROFILE);
		}

		if (request.nickname() != null && !request.nickname().isBlank()) {
			Preconditions.validate(user.getNicknameChangeCount() < 3, ErrorCode.INVALID_NICKNAME_CHANGE);
			Preconditions.validate(!user.getNickname().equals(request.nickname()), ErrorCode.SAME_NICKNAME_NOT_ALLOWED);

			newNickname = request.nickname();
			user.updateNicknameCount();
		}

		if (request.occupation() != null)	newOccupation = request.occupation();

		if (request.gender() != null) 	newGender = request.gender();

		if (request.age() != null) 	newAge = request.age();

		user.updateUserProfile(newProfileImageUrl, newNickname, newOccupation, newAge, newGender);
		return UserUpdateProfileResponse.updateProfile(userId, newProfileImageUrl, newNickname,  newOccupation, newAge, newGender);
	}

	public UserProfileResponse getProfile(final UUID userId) {
		User user =  userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		return UserProfileResponse.from(user);
	}

	@Transactional
	public void withdrawUser(final UUID userId) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		if (user.isActive()) {
			jwtService.deleteToken(userId);
			user.withdrawUser();

			log.info("success to withdraw user information");
		} else {
			log.error("fail to withdraw user information");
			throw new CustomException(ErrorCode.INVALID_ACCESS);
		}
	}

	@Scheduled(cron = "0 0 0 1 * *")
	@Transactional
	public void resetNicknameChangeCount(){
		userRepository.resetNicknameChangeCount();
		log.info("reset nickname change count for all users");
	}
}
