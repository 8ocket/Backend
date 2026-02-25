package com.kt.mindLog.service.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.enums.LoginType;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.user.response.LoginResponse;
import com.kt.mindLog.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	@Transactional
	public LoginResponse login(final String email, final LoginType loginType) {

		Optional<User> findUser = userRepository.findByEmailAndLoginType(email, loginType);

		if (findUser.isPresent()) {
			//기존 사용자
			//return jwt 생성 로직
		}

		//신규 사용자
		User newUser = User.builder()
			.email(email)
			.loginType(loginType)
			.build();

		userRepository.save(newUser);
		//return jwt 생성 로직
	}
}
