package com.kt.mindLog.util.fixture;

import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.persona.Persona;
import com.kt.mindLog.domain.user.User;

public abstract class SessionFixture {

	public static Session initSession(User user, Persona persona) {
		return buildSession(user, persona);
	}

	protected static Session buildSession(User user, Persona persona) {
		Session session = Session.builder()
			.user(user)
			.persona(persona)
			.build();

		ReflectionTestUtils.setField(session, "id", UUID.randomUUID());

		return session;
	}
}
