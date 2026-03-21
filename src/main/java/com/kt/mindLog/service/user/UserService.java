package com.kt.mindLog.service.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kt.mindLog.domain.user.LoginType;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.user.request.UserCreateRequest;
import com.kt.mindLog.dto.user.response.LoginResponse;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.repository.UserRepository;
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

	@Transactional
	public LoginResponse login(final String email, final LoginType loginType) {

		Optional<User> findUser = userRepository.findByEmailAndLoginType(email, loginType);

		if (findUser.isPresent()) {
			findUser.get().updateLastLoginAt();
			//TODO 3 credit 부여
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

		String profileImageUrl = s3Service.uploadProfileImage(profile);

		user.updateUserInfo(
			request.nickname(),
			profileImageUrl,
			request.occupation(),
			request.age(),
			request.gender()
		);

		userRepository.save(user);
		log.info("success to update user information");
	}
}
