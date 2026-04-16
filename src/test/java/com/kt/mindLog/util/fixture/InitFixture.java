package com.kt.mindLog.util.fixture;

import org.junit.jupiter.api.BeforeEach;

import com.kt.mindLog.domain.credit.Credit;
import com.kt.mindLog.domain.persona.Persona;

public abstract class InitFixture extends UserFixture {

	protected Persona persona;
	protected Credit credit;

	@BeforeEach
	void initFixture() {
		persona = PersonaFixture.initPersona();
		credit = CreditFixture.initCredit(user);
	}
}
