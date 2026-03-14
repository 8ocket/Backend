package com.kt.mindLog.domain.persona;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

import com.kt.mindLog.domain.session.Session;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "ai_personas")
@NoArgsConstructor
public class Persona{

	@Id
	@UuidGenerator(style = UuidGenerator.Style.VERSION_7)
	@Column(name = "persona_id")
	private UUID id;

	@Column(length = 50, nullable = false)
	private String personaType;

	@Column(length = 100, nullable = false)
	private String personaName;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(columnDefinition = "TEXT")
	private String toneSettings;

	private Integer unlockCredits;

	private boolean isDefault;

	private boolean isActive;

	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "persona")
	private List<Session> sessions = new ArrayList<>();

	@Builder
	private Persona(String personaType, String personaName, String description, String toneSettings,
		Integer unlockCredits, boolean isDefault) {
		this.personaType = personaType;
		this.personaName = personaName;
		this.description = description;
		this.toneSettings = toneSettings;
		this.unlockCredits = unlockCredits;
		this.isDefault = isDefault;
		this.isActive = true;
		this.createdAt = LocalDateTime.now();
	}
}
