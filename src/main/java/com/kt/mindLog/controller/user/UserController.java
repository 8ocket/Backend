package com.kt.mindLog.controller.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kt.mindLog.dto.user.request.UserCreateRequest;
import com.kt.mindLog.dto.user.request.UserUpdateRequest;
import com.kt.mindLog.dto.user.response.UserProfileResponse;
import com.kt.mindLog.dto.user.response.UserUpdateProfileResponse;
import com.kt.mindLog.global.annotation.Login;
import com.kt.mindLog.global.common.response.ApiResult;
import com.kt.mindLog.global.security.auth.CustomUser;
import com.kt.mindLog.service.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

	private final UserService userService;

	@PatchMapping("/signup")
	public ApiResult<Void> createUser(@Login CustomUser user, @RequestPart("profile_image") MultipartFile profileImage,
		@Valid @RequestPart("contents") final UserCreateRequest request) {
		userService.createUserInfo(user.getId(), profileImage, request);
		return ApiResult.ok();
	}

	@PatchMapping("/me/profile")
	public UserUpdateProfileResponse updateProfile(@Login CustomUser user, @RequestPart("profile_image") MultipartFile profileImage,
		@Valid @RequestPart("contents") final UserUpdateRequest request) {
		return userService.updateProfile(user.getId(), profileImage, request);
	}

	@GetMapping("/me/profile")
	public UserProfileResponse getProfile(@Login CustomUser user) {
		return userService.getProfile(user.getId());
	}
}
