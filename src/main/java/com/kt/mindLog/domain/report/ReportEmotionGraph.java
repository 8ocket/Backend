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
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "report_emotion_graphs")
@NoArgsConstructor
public class ReportEmotionGraph {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "graph_id")
	private UUID id;

	private UUID sessionId;

	private Integer avgScore;

	private boolean isInflectionPoint;

	private String inflectionType;

	private LocalDateTime recordedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id", nullable = false)
	private Report report;

	@Builder
	public ReportEmotionGraph(final UUID sessionId, final Integer avgScore, final boolean isInflectionPoint,
		final String inflectionType, final LocalDateTime recordedAt, final Report report) {

		this.sessionId = sessionId;
		this.avgScore = avgScore;
		this.isInflectionPoint = isInflectionPoint;
		this.inflectionType = inflectionType;
		this.recordedAt = recordedAt;
		this.report = report;
	}
}
