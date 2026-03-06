package com.kt.mindLog.global.security;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kt.mindLog.domain.enums.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CustomUser implements UserDetails {
	private final Long id;
	private final Role role;

	@Builder
	public CustomUser(Long id, Role role) {
		this.id = id;
		this.role = role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role));
	}

	@Override
	public String getUsername() {
		return id.toString();
	}

	@Override
	public @Nullable String getPassword() {
		return "";
	}
}
