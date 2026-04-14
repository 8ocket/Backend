package com.kt.mindLog.service.summary;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.kt.mindLog.domain.session.Session;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmotionEventListener {

	private final SummaryService summaryService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(Session session) {
		summaryService.updatePrimaryEmotion(session);
	}
}
