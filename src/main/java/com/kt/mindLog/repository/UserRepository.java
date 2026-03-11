package com.kt.mindLog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.enums.LoginType;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmailAndLoginType(String email, LoginType loginType);

	default User findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	};
}
