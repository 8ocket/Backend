package com.kt.mindLog.dto.attendance.response;

import java.time.LocalDate;
import java.util.List;

public record AttendanceResponse(
	List<LocalDate> attendanceDates
) {
}
