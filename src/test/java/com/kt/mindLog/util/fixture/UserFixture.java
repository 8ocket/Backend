package com.kt.mindLog.util.fixture;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;

import com.kt.mindLog.domain.user.Gender;
import com.kt.mindLog.domain.user.LoginType;
import com.kt.mindLog.domain.user.Occupation;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.global.security.auth.CustomUser;

public abstract class UserFixture {

	protected User user;
	protected CustomUser customUser;

	private static final String DEFAULT_PROFILE_URL = "https://s3.example.com/default-profile.png";

	@BeforeEach
	void setUpUser() {
		user = buildActiveUser("test@kakao.com", LoginType.KAKAO, DEFAULT_PROFILE_URL);
		customUser = CustomUser.builder()
			.id(UUID.randomUUID())
			.role(user.getRole())
			.build();
	}

	public static User buildInactiveUser(String email, LoginType loginType) {
		User user = User.builder()
			.email(email)
			.loginType(loginType)
			.build();

		ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

		return user;
	}

	public static User buildActiveUser(String email, LoginType loginType, String profileUrl) {
		User user = buildInactiveUser(email, loginType);
		user.updateUserInfo("테스터", profileUrl, Occupation.STUDENT, 25, Gender.MALE);

		return user;
	}
}
