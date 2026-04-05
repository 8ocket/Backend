package com.kt.mindLog.controller.attendance;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.dto.attendance.response.AttendanceResponse;
import com.kt.mindLog.global.annotation.Login;
import com.kt.mindLog.global.security.CustomUser;
import com.kt.mindLog.service.attendance.AttendanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {
	private final AttendanceService attendanceService;

	@GetMapping("/me")
	public AttendanceResponse getAttendance(@Login CustomUser user) {
		return attendanceService.getAttendance(user.getId());
	}
}
