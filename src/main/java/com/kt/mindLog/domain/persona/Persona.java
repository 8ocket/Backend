package com.kt.mindLog.domain.persona;

import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

	@Column(columnDefinition = "TEXT")
	private String personaImageUrl;

	private Integer unlockCredits;

	private boolean isDefault;

	private boolean isActive;

	private LocalDateTime createdAt;

	@Builder
	public Persona(String personaType, String personaName, String description, String toneSettings,
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

	public void updatePersonaImageUrl(String personaImageUrl) {
		this.personaImageUrl = personaImageUrl;
	}
}
