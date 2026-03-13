package com.kt.mindLog.domain.persona;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.UuidGenerator;

import com.kt.mindLog.domain.session.Session;

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
	private String id;

	@OneToMany(mappedBy = "persona")
	private List<Session> sessions = new ArrayList<>();

	@Builder
	public Persona(final String id) {
		this.id = id;
	}
}
