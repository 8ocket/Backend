package com.kt.mindLog.domain.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "user_id")
	private UUID id;

	@Column(length = 100)
	private String email;

	@Column(length = 30)
	private String nickname;

	@Column(columnDefinition = "TEXT")
	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	private Occupation occupation;

	@Enumerated(EnumType.STRING)
	private	Gender gender;

	private Integer age;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	private boolean isActive;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LoginType loginType;

	@Column(updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime lastLoginAt;

	private String passwordHash;

	private Integer nicknameChangeCount;

	@Builder
	private User(final String email, final LoginType loginType) {
		this.email = email;
		this.loginType = loginType;
		this.role = Role.USER;
		this.isActive = false;
		this.createdAt = LocalDateTime.now();
		this.lastLoginAt = LocalDateTime.now();
		this.nicknameChangeCount = 0;
	}

	public void updateLastLoginAt() {
		this.lastLoginAt = LocalDateTime.now();
	}

	public void updateUserInfo(final String nickname, final String profileImageUrl,
		final Occupation occupation, final Integer age, final Gender gender) {
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
		this.occupation = occupation;
		this.age = age;
		this.gender = gender;
		this.isActive = true;
	}

	public void updateUserProfile(final String profileImageUrl, final String nickname,
		final Occupation occupation, final Integer age, final Gender gender) {
		this.profileImageUrl = profileImageUrl;
		this.nickname = nickname;
		this.occupation = occupation;
		this.age = age;
		this.gender = gender;
	}

	public void updateNicknameCount() {
		this.nicknameChangeCount = this.nicknameChangeCount + 1;
	}

	public void withdrawUser() {
		this.isActive = false;
		this.email = "withdrawn_" + this.id;
	}
}
