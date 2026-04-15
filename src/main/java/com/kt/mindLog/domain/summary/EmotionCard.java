package com.kt.mindLog.domain.summary;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.kt.mindLog.domain.session.Session;
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
@Table(name = "emotion_cards")
@NoArgsConstructor
public class EmotionCard {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "card_id")
	private UUID id;

	@Column(columnDefinition = "TEXT")
	private String backImageUrl;

	@Column(columnDefinition = "TEXT")
	private String frontImageUrl;

	private String interpretation;

	private String generationMetadata;

	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id", nullable = false)
	private Session session;

	@Builder
	public EmotionCard(final String backImageUrl, final String frontImageUrl,
		final User user, final Session session) {
		this.backImageUrl = backImageUrl;
		this.frontImageUrl = frontImageUrl;
		this.user = user;
		this.session = session;
		this.createdAt = LocalDateTime.now();
	}

	public void updateImageUrl(String frontImageUrl, String backImageUrl) {
		this.frontImageUrl = frontImageUrl;
		this.backImageUrl = backImageUrl;
	}
}
