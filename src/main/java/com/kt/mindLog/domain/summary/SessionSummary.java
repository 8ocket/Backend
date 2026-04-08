package com.kt.mindLog.domain.summary;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.global.common.support.BaseEntity;

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
@NoArgsConstructor
@Table(name = "session_summaries")
public class SessionSummary extends BaseEntity {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "summary_id")
	private UUID id;

	@Column(columnDefinition = "TEXT")
	private String fact;

	@Column(columnDefinition = "TEXT")
	private String emotion;

	@Column(columnDefinition = "TEXT")
	private String insight;

	private boolean isEdited;

	@Column(nullable = true)
	private String visibility;

	@Column(nullable = true)
	private String cardImageUrl;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id", nullable = false)
	private Session session;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public SessionSummary(final String fact, final String emotion, final String insight,
		final Session session, final User user) {
		this.fact = fact;
		this.emotion = emotion;
		this.insight = insight;
		this.isEdited = false;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		this.session = session;
		this.user = user;
	}

	public void updateCardImageUrl(String cardImageUrl) {
		this.cardImageUrl = cardImageUrl;
	}

	public void updateSummaryCard(String emotion, String fact, String insight) {
		if (emotion != null) this.emotion = emotion;
		if (fact != null) this.fact = fact;
	    if (insight != null) this.insight = insight;
		this.isEdited = true;
	}
}
