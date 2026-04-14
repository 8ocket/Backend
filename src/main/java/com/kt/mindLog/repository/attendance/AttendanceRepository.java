package com.kt.mindLog.repository.attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.attendance.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
	boolean existsByUser_IdAndAttendanceDate(UUID userId, LocalDate attendanceDate);

	@Query("SELECT a.attendanceDate FROM Attendance a WHERE a.user.id = :userId AND a.attendanceDate BETWEEN :start AND :end")
	List<LocalDate> findByUserIdAndAttendanceDateBetween(UUID userId, LocalDate start, LocalDate end);
}
