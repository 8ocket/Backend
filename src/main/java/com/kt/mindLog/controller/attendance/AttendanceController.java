package com.kt.mindLog.controller.attendance;

import java.time.YearMonth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mindLog.dto.attendance.response.AttendanceResponse;
import com.kt.mindLog.global.annotation.Login;
import com.kt.mindLog.global.security.auth.CustomUser;
import com.kt.mindLog.service.attendance.AttendanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {
	private final AttendanceService attendanceService;

	@GetMapping("/me/{yearMonth}")
	public AttendanceResponse getAttendance(@Login CustomUser user, @PathVariable YearMonth yearMonth) {
		return attendanceService.getAttendance(user.getId(), yearMonth);
	}
}
