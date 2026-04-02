package com.kt.mindLog.domain.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.kt.mindLog.domain.user.User;

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
@Table(name = "ai_reports")
@NoArgsConstructor
public class Report {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "report_id")
	private UUID id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportType reportType;

	@Column(nullable = false)
	private LocalDate periodStart;

	@Column(nullable = false)
	private LocalDate periodEnd;

	private Integer sessionCount;

	@Enumerated(EnumType.STRING)
	private ReportStatus status;

	private LocalDateTime createdAt;

	private boolean isViewed;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public Report(final ReportType reportType, final LocalDate periodStart, final LocalDate periodEnd,
		final Integer sessionCount, final User user) {
		this.reportType = reportType;
		this.periodStart = periodStart;
		this.periodEnd = periodEnd;
		this.sessionCount = sessionCount;
		this.status = ReportStatus.GENERATING;
		this.createdAt = LocalDateTime.now();
		this.isViewed = false;
		this.user = user;
	}
}
