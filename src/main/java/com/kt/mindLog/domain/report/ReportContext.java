package com.kt.mindLog.domain.report;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "report_complete_event")
@NoArgsConstructor
public class ReportContext {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "context_id")
	private UUID id;

	private Integer sessionCount;

	private String graphEvaluation;

	private String topicEvaluation;

	private String currentStatus;

	private String tendency;

}
