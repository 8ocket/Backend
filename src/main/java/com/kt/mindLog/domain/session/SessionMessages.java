package com.kt.mindLog.domain.session;

import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;

import com.kt.mindLog.domain.user.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class SessionMessages {
	@Id
	@UuidGenerator
    private String id;

	@Enumerated(EnumType.STRING)
	private Role role;

	private String content;

	@Column(updatable = false)
	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id", nullable = false)
	private Session session;

	@Builder
	public SessionMessages(Role role, String content, Session session) {
		this.role = role;
		this.content = content;
		this.session = session;
		this.createdAt = LocalDateTime.now();
	}
}
