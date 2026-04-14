package com.kt.mindLog.global.security.jwt;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.global.security.auth.AuthToken;
import com.kt.mindLog.global.security.auth.CustomUser;
import com.kt.mindLog.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private static final String TOKEN_PREFIX = "Bearer ";
	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		var header = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (header == null || !header.startsWith(TOKEN_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		var token = header.substring(TOKEN_PREFIX.length());

		try {
			jwtProvider.validateToken(token);

			var user = jwtProvider.getUserDetail(token);

			var getUser = userRepository.findByIdOrThrow(user.getId(), ErrorCode.NOT_FOUND_USER);
			if (!getUser.getEmail().startsWith("withdrawn")) {
				var authentication = new AuthToken(user, user.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
				request.setAttribute("CustomUser", CustomUser.builder().id(user.getId()).role(user.getRole()).build());
			}
		} catch (Exception e) {
			new JwtAuthenticationEntryPoint().commence(request, response,
				new BadCredentialsException(ErrorCode.INVALID_USER.getMessage()));
			return;
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return request.getServletPath().equals("/v1/auth/logout");
	}
}
