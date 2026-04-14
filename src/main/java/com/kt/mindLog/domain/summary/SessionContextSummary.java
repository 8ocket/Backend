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
@Table(name = "session_context_summaries")
@NoArgsConstructor
public class SessionContextSummary extends BaseEntity {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "context_id")
	private UUID id;

	@Column(columnDefinition = "TEXT")
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id", nullable = false)
	private Session session;

	@Builder
	public SessionContextSummary(final String content, final User user, final Session session) {
		this.content = content;
		this.user = user;
		this.session = session;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
}
