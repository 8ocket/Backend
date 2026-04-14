package com.kt.mindLog.domain.session;

import java.time.LocalDateTime;
import java.util.UUID;

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
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "session_messages")
public class SessionMessages {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "message_id")
    private UUID id;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(columnDefinition = "TEXT")
	private String content;

	private Integer sequenceNum;

	private boolean isMeaningful;

	@Column(updatable = false)
	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id", nullable = false)
	private Session session;

	@Builder
	public SessionMessages(Role role, String content, Session session,  Integer sequenceNum) {
		this.role = role;
		this.content = content;
		this.session = session;
		this.sequenceNum = sequenceNum;
		this.createdAt = LocalDateTime.now();
	}
}
