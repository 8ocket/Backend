package com.kt.mindLog.controller.summary;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kt.mindLog.dto.summary.request.SummaryCardUpdateRequest;
import com.kt.mindLog.dto.summary.response.SummaryCardListResponse;
import com.kt.mindLog.dto.summary.response.SummaryCardResponse;
import com.kt.mindLog.dto.summary.response.SummaryCardUpdateResponse;
import com.kt.mindLog.global.annotation.Login;
import com.kt.mindLog.global.common.response.ApiResult;
import com.kt.mindLog.global.security.auth.CustomUser;
import com.kt.mindLog.service.summary.SummaryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/summaries")
public class SummaryController {
	private final SummaryService summaryService;

	@PatchMapping("/{summaryId}/image")
	public ApiResult<Void> uploadSummaryCard(@Login CustomUser user, @PathVariable UUID summaryId,
		@RequestPart("card_front_image") MultipartFile cardFrontImage,
		@RequestPart("card_back_image") MultipartFile cardBackImage) {
		summaryService.uploadSummaryCard(user.getId(), summaryId, cardFrontImage, cardBackImage);

		return ApiResult.ok();
	}

	@GetMapping("/{summaryId}")
	public ApiResult<SummaryCardResponse> getSummaryCard(@Login CustomUser user, @PathVariable UUID summaryId) {
		return ApiResult.ok(summaryService.getSummaryCard(user.getId(), summaryId));
	}

	@PutMapping("/{summaryId}")
	public ApiResult<SummaryCardUpdateResponse> updateSummaryCard(@Login CustomUser user,
		@PathVariable UUID summaryId, @RequestBody SummaryCardUpdateRequest request) {
		return ApiResult.ok(summaryService.updateSummaryCard(user.getId(), summaryId, request));
	}

	@GetMapping
	public ApiResult<Page<SummaryCardListResponse>> getSummaryCardList(@Login CustomUser user, Pageable pageable) {
		return ApiResult.ok(summaryService.getSummaryCardList(user.getId(), pageable));
	}
}
