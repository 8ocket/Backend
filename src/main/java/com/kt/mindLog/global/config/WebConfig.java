package com.kt.mindLog.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kt.mindLog.global.resolver.LoginResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final LoginResolver loginResolver;

	public WebConfig(final LoginResolver loginResolver) {
		this.loginResolver = loginResolver;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(loginResolver);
	}
}

