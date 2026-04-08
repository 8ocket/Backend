package com.kt.mindLog.repository.summary;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.summary.EmotionCard;

@Repository
public interface EmotionCardRepository extends JpaRepository<EmotionCard, UUID> {
	void deleteBySessionId(UUID sessionId);
}
