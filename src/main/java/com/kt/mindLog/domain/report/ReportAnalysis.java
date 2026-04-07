package com.kt.mindLog.domain.report;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

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
@Table(name = "report_analysis")
@NoArgsConstructor
public class ReportAnalysis {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "analysis_id")
	private UUID id;

	@Column(columnDefinition = "TEXT")
	private String currentStatus;

	@Column(columnDefinition = "TEXT")
	private String tendencySummary;

	@Column(columnDefinition = "TEXT")
	private String graphEvaluation;

	@Column(columnDefinition = "TEXT")
	private String topicEvaluation;

	private LocalDateTime createdAt;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id", nullable = false)
	private Report report;

	@Builder
	public ReportAnalysis(final String currentStatus, final String tendencySummary, final String graphEvaluation,
		final String topicEvaluation, final Report report) {
		this.currentStatus = currentStatus;
		this.tendencySummary = tendencySummary;
		this.graphEvaluation = graphEvaluation;
		this.topicEvaluation = topicEvaluation;
		this.createdAt = LocalDateTime.now();
		this.report = report;
	}
}
