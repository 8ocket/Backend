package com.kt.mindLog.repository.summary;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.mindLog.domain.summary.EmotionCard;

public interface EmotionCardRepository extends JpaRepository<EmotionCard, UUID> {
}
