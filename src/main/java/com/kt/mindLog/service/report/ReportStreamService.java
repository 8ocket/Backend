package com.kt.mindLog.service.report;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import com.kt.mindLog.dto.report.request.AiReportCreateRequest;
import com.kt.mindLog.dto.report.response.AiReportResponse;
import com.kt.mindLog.global.property.StreamProperties;
import com.kt.mindLog.service.sse.SSEService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportStreamService {

	private final SSEService sseService;
	private final ReportPersistenceService reportPersistenceService;

	private final StreamProperties streamProperties;

	private final ObjectMapper objectMapper;

	public Flux<Object> receiveSSE(final AiReportCreateRequest contents, final UUID reportId) {

		return sseService.streamSSE(streamProperties.getReportUri()+"/generate", null, contents)
			.handle((event, sink) -> handleMessageEvent(event, sink, reportId))
			.doOnError(e -> log.error("스트림 오류", e));
	}

	private void handleMessageEvent(final ServerSentEvent<String> event, final SynchronousSink<Object> sink, final UUID reportId) {
		switch (event.event()) {
			case "status" -> sink.next(event);

			case "ai_complete" -> {

				try {
					var report = objectMapper.readValue(event.data(), AiReportResponse.class);
					reportPersistenceService.saveReport(report, reportId);
					//TODO credit 차감

					sink.next(ServerSentEvent.builder()
						.event("ai_complete")
						.data(Map.of(
							"report_id", reportId,
							"created_at", LocalDate.now()
						))
						.build());
				} catch (Exception e) {
					log.error("리포트 생성 실패 | reportId={}", reportId, e);

					sink.next(ServerSentEvent.builder()
						.event("server_error")
						.data(Map.of(
							"content", "failed to create report"
						))
						.build());
				} finally {
					sink.complete();
				}
			}

			case "error", "done" -> {
				sink.next(event);
				sink.complete();
			}
		}
	}
}
