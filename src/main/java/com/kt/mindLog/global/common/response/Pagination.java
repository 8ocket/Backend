package com.kt.mindLog.global.common.response;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Pagination(
	int page,

	int size,

	@JsonProperty("total_count")
	long totalCount,

	@JsonProperty("total_pages")
	int totalPages
) {
	public static Pagination from(Page page) {
		return new Pagination(
			page.getPageable().getPageNumber() + 1,
			page.getPageable().getPageSize(),
			page.getTotalElements(),
			page.getTotalPages()
		);
	}
}
