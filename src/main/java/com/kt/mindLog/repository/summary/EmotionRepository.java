package com.kt.mindLog.repository.summary;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.summary.Emotion;
import com.kt.mindLog.domain.summary.EmotionType;

@Repository
public interface EmotionRepository extends JpaRepository<Emotion, UUID> {
	List<Emotion> findBySessionId(UUID sessionId);

	@Query("""
		SELECT e.emotionType
		FROM Emotion e
		WHERE e.session = :session
		GROUP BY e.emotionType
		ORDER BY COUNT(e) DESC
		LIMIT 1
		""")
	EmotionType findTopEmotionType(Session session);

	@Query("""
		SELECT COALESCE(SUM(e.intensity), 0)
		FROM Emotion e
		WHERE e.session.id = :sessionId
		""")
	Integer sumIntensityBySessionId(UUID sessionId);
}
