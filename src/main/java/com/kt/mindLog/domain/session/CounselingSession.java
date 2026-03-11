package com.kt.mindLog.domain.session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.global.common.support.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class CounselingSession extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SessionStatus status;

	private LocalDateTime endedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(mappedBy = "session")
	private List<SessionMessage> messages = new ArrayList<>();

	@Builder
	public CounselingSession(User user) {
		this.user = user;
		this.status = SessionStatus.ACTIVE;
	}
}
