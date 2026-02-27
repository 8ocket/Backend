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
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "USER_SEQ")
	private Long id;

	@Column(length = 30)
	private String email;

	@Column(length = 30)
	private String nickname;

	private String profileImageUrl;

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
}
