package com.kt.mindLog.global.security;

import java.io.IOException;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private static final String TOKEN_PREFIX = "Bearer ";
	private final JwtProvider jwtProvider;


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		var header = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (Strings.isBlank(header)) {
			filterChain.doFilter(request, response);
			return;
		}

		var token = header.substring(TOKEN_PREFIX.length());

		try {
			jwtProvider.validateToken(token);

			var user = jwtProvider.getUserDetail(token);
			var authentication = new AuthToken(user, user.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(authentication);

			request.setAttribute("CustomUser", CustomUser.builder().id(user.getId()).role(user.getRole()).build());
		} catch (Exception e) {
			throw new CustomException(ErrorCode.INVALID_JWT_TOKEN_FORMAT);
		}

		filterChain.doFilter(request, response);
	}
}
