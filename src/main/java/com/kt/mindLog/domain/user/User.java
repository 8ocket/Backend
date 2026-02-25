package com.kt.mindLog.domain.user;

import java.time.LocalDateTime;

import com.kt.mindLog.domain.enums.LoginType;
import com.kt.mindLog.domain.enums.Role;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Column(nullable = false, length = 30)
	private String email;

	// @Column(nullable = false, length = 256)
	// private String password;

	@Column(nullable = false, length = 30)
	private String nickname;

	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	private String onboardingData;

	private boolean isActive;

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
}
