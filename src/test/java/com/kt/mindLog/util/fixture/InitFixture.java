package com.kt.mindLog.util.fixture;

import org.junit.jupiter.api.BeforeEach;

import com.kt.mindLog.domain.credit.Credit;
import com.kt.mindLog.domain.persona.Persona;
import com.kt.mindLog.domain.session.Session;

public abstract class InitFixture extends UserFixture {

	protected Persona persona;
	protected Credit credit;
	protected Session session;

	@BeforeEach
	void initFixture() {
		persona = PersonaFixture.initPersona();
		credit = CreditFixture.initCredit(user);
		session = SessionFixture.initSession(user, persona);
	}
}
