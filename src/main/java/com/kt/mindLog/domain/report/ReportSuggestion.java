package com.kt.mindLog.domain.report;

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
@Table(name = "suggestion_item")
@NoArgsConstructor
public class ReportSuggestion {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "suggestion_id")
	private UUID id;

	private String suggestionType;

	private String content;

	private Integer priority;

	@Column(nullable = true)
	private String extraMetadata;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id", nullable = false)
	private Report report;

	@Builder
	public ReportSuggestion(String suggestionType, String content, Integer priority, Report report) {
		this.suggestionType = suggestionType;
		this.content = content;
		this.priority = priority;
		this.report = report;
	}
}
