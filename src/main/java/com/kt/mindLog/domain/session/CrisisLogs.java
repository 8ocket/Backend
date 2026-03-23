package com.kt.mindLog.domain.session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.kt.mindLog.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "crisis_logs")
@NoArgsConstructor
public class CrisisLogs {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "log_id")
	private UUID id;

	@Column(name = "crisis_level")
	private Integer level;

	@Column(columnDefinition = "TEXT", name = "detected_keywords")
	private List<String> keywords;

	@Column(columnDefinition = "TEXT", name = "response_message")
	private String message;

	private LocalDateTime detectedAt;

	@Column(name = "user_acknowledged", nullable = false)
	private boolean isAcknowledged;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id", nullable = false)
	private Session session;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "message_id", nullable = false)
	private SessionMessages messages;

	@Builder
	public CrisisLogs(Integer level, List<String> keywords, String message,
		User user, Session session, SessionMessages messages) {
		this.level = level;
		this.keywords = keywords;
		this.message = message;
		this.detectedAt = LocalDateTime.now();
		this.isAcknowledged = false;
		this.user = user;
		this.session = session;
		this.messages = messages;
	}
}
