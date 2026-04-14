package com.kt.mindLog.global.security.auth;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class AuthToken extends AbstractAuthenticationToken {
	private final CustomUser customUser;

	public AuthToken(CustomUser customUser,
		Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		super.setAuthenticated(true);
		this.customUser = customUser;
	}

	@Override
	public Object getCredentials() {
		return customUser.getId();
	}

	@Override
	public Object getPrincipal() {
		return customUser;
	}
}
