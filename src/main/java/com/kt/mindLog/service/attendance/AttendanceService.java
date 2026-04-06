package com.kt.mindLog.service.attendance;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kt.mindLog.domain.attendance.Attendance;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.attendance.response.AttendanceResponse;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.repository.UserRepository;
import com.kt.mindLog.repository.attendance.AttendanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {
	private final AttendanceRepository attendanceRepository;
	private final UserRepository userRepository;

	public AttendanceResponse getAttendance(UUID userId, YearMonth month) {
		LocalDate start = month.atDay(1);
		LocalDate end = month.atEndOfMonth();

		List<LocalDate> attendanceDates = attendanceRepository.findByUserIdAndAttendanceDateBetween(userId, start, end);

		return new AttendanceResponse(attendanceDates);
	}

	public void saveAttendance(UUID userId) {
		User user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		boolean alreadyChecked = attendanceRepository.existsByUser_IdAndAttendanceDate(userId, LocalDate.now());

		if (!alreadyChecked) {
			attendanceRepository.save(Attendance.builder().user(user).build());
		}
	}
}
