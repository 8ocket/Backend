package com.kt.mindLog.domain.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
	@UuidGenerator
	private String id;

	@Column(length = 30)
	private String email;

	@Column(length = 30)
	private String nickname;

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

	@Builder
	private User(String email, LoginType loginType) {
		this.email = email;
		this.loginType = loginType;
		this.role = Role.USER;
		this.isActive = true;
		this.createdAt = LocalDateTime.now();
		this.lastLoginAt = LocalDateTime.now();
	}

	public void updateLastLoginAt() {
		this.lastLoginAt = LocalDateTime.now();
	}

	public void updateUserInfo(String nickname, String profileImageUrl,
		Occupation occupation, Integer age, Gender gender) {
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
		this.occupation = occupation;
		this.age = age;
		this.gender = gender;
	}
}
