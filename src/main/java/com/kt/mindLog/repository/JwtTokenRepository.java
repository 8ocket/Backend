package com.kt.mindLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.auth.JwtToken;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
}
