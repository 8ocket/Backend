package com.kt.mindLog.service.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.enums.LoginType;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.user.LoginResponse;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.service.auth.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final AuthService authService;

	@Transactional
	public LoginResponse login(final String email, final LoginType loginType) {

		Optional<User> findUser = userRepository.findByEmailAndLoginType(email, loginType);

		if (findUser.isPresent()) {
			findUser.get().updateLastLoginAt();
			return authService.createJwtTokens(findUser.get(), false);
		}

		User newUser = User.builder()
			.email(email)
			.loginType(loginType)
			.build();

		userRepository.save(newUser);

		return authService.createJwtTokens(newUser, true);
	}
}
