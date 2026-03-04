package com.kt.mindLog.global.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.kt.mindLog.global.annotation.Login;
import com.kt.mindLog.global.security.CustomUser;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class LoginResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean isMemberSessionType = parameter.getParameterType().equals(CustomUser.class);
		boolean isLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
		return isMemberSessionType && isLoginAnnotation;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
		return request.getAttribute("CustomUser");
	}
}
