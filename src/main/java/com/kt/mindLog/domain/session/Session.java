package com.kt.mindLog.domain.session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.kt.mindLog.domain.persona.Persona;
import com.kt.mindLog.domain.summary.SessionContextSummary;
import com.kt.mindLog.domain.summary.SessionSummary;
import com.kt.mindLog.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "counseling_sessions")
@NoArgsConstructor
public class Session {

	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "session_id")
	private UUID id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SessionStatus status;

	private String title;

	private LocalDateTime startedAt;

	private LocalDateTime endedAt;

	@Column(updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "persona_id", nullable = false)
	private Persona persona;

	@OneToMany(mappedBy = "session")
	private List<SessionMessages> messages = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "summary_id")
	private SessionSummary summary;

	@Builder
	public Session(User user, Persona persona) {
		this.user = user;
		this.persona = persona;
		this.status = SessionStatus.ACTIVE;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		this.startedAt = LocalDateTime.now();
	}

	public void updateTitle(final String title) {
		this.title = title;
	}

	public void updateStatus(final SessionStatus sessionStatus) {
		this.status = sessionStatus;
		this.endedAt = LocalDateTime.now();
	}

	public void updateTime() {
		this.updatedAt = LocalDateTime.now();
	}

	public void updateSummary(final SessionSummary summary) {
		this.summary = summary;
	}

	public void clearSummary() {
		this.summary = null;
	}
}
