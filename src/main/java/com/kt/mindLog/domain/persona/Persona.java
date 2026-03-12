package com.kt.mindLog.domain.persona;

import java.util.ArrayList;
import java.util.List;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.global.common.support.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "ai_personas")
@NoArgsConstructor
public class Persona extends BaseEntity {
	@OneToMany(mappedBy = "persona")
	private List<Session> sessions = new ArrayList<>();
}
