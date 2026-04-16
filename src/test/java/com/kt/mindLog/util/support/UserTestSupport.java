package com.kt.mindLog.util.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mindLog.domain.user.LoginType;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.util.fixture.UserFixture;

@SpringBootTest
@Transactional
public abstract class UserTestSupport extends UserFixture {

	@Autowired
	protected UserRepository userRepository;

	protected User user;

	@BeforeEach
	void setUp() {
		user = initUser("test@kakao.com", LoginType.KAKAO);
	}

	protected User initUser(String email, LoginType loginType) {
		return userRepository.save(buildInactiveUser(email, loginType));
	}
}
