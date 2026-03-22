package com.kt.mindLog.global.common.request;

import org.springframework.data.domain.PageRequest;

public record Paging(
	Integer page,
	Integer size
) {
	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 20;

	public PageRequest toPageable() {
		int safePage = (page == null || page < 1) ? DEFAULT_PAGE : page;
		int safeSize = (size == null || size < 1) ? DEFAULT_SIZE : size;

		return PageRequest.of(safePage - 1, safeSize);
	}
}
