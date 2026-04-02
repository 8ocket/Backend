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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "topic_item")
@NoArgsConstructor
public class ReportTopic {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "topic_id")
	private UUID id;

	private String name;

	private String category;

	private Integer count;

	private String pattern;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "context_id", nullable = false)
	private ReportContext context;
}
