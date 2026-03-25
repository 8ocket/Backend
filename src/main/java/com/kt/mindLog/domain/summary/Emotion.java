package com.kt.mindLog.domain.summary;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.session.SessionMessages;
import com.kt.mindLog.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "emotion_extractions")
@NoArgsConstructor
public class Emotion {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "extraction_id")
	private UUID id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EmotionType emotionType;

	private Integer intensity;

	private boolean isPrimary;

	private LocalDateTime createdAt;

	@Column(name = "source_keyword")
	private String keyword;

	@Column(name = "trigger_context", columnDefinition = "TEXT")
	private String trigger;

	@Enumerated(EnumType.STRING)
	@Column(name = "emotion_trajectory")
	private EmotionTrajectory trajectory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id", nullable = false)
	private Session session;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "message_id", nullable = false)
	private SessionMessages message;

	@Builder
	public Emotion(EmotionType emotionType, Integer intensity, String keyword, String trigger, EmotionTrajectory trajectory,
		User user, Session session, SessionMessages message) {
		this.emotionType = emotionType;
		this.intensity = intensity;
		this.createdAt = LocalDateTime.now();
		this.keyword = keyword;
		this.trigger = trigger;
		this.trajectory = trajectory;
		this.user = user;
		this.session = session;
		this.message = message;
		this.isPrimary = false;
	}

	public void updatePrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}
}
