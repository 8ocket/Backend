package com.kt.mindLog.domain.persona;

import java.util.ArrayList;
import java.util.List;

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
	protected Long id;

	@OneToMany(mappedBy = "persona")
	private List<Session> sessions = new ArrayList<>();

	@Builder
	public Persona(final Long id) {
		this.id = id;
	}
}
