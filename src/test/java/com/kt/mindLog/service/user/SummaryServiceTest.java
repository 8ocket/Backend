package com.kt.mindLog.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.kt.mindLog.domain.session.Session;
import com.kt.mindLog.domain.summary.EmotionCard;
import com.kt.mindLog.domain.summary.SessionSummary;
import com.kt.mindLog.domain.user.User;
import com.kt.mindLog.dto.summary.request.SummaryCardUpdateRequest;
import com.kt.mindLog.dto.summary.response.SummaryCardListResponse;
import com.kt.mindLog.dto.summary.response.SummaryCardResponse;
import com.kt.mindLog.dto.summary.response.SummaryCardUpdateResponse;
import com.kt.mindLog.global.common.exception.CustomException;
import com.kt.mindLog.global.common.exception.ErrorCode;
import com.kt.mindLog.repository.summary.EmotionCardRepository;
import com.kt.mindLog.repository.summary.SummaryRepository;
import com.kt.mindLog.service.s3.S3Path;
import com.kt.mindLog.service.s3.S3Service;
import com.kt.mindLog.service.summary.SummaryService;
import com.kt.mindLog.util.fixture.UserFixture;
import com.kt.mindLog.domain.user.LoginType;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

	@InjectMocks
	private SummaryService summaryService;

	@Mock
	private SummaryRepository summaryRepository;

	@Mock
	private EmotionCardRepository emotionCardRepository;

	@Mock
	private S3Service s3Service;

	private static final String DEFAULT_PROFILE_URL = "https://s3.example.com/default-profile.png";

	private UUID userId;
	private UUID otherUserId;
	private UUID summaryId;
	private User user;
	private User otherUser;
	private Session session;

	@BeforeEach
	void setUp() {
		user = UserFixture.buildActiveUser("test@kakao.com", LoginType.KAKAO, DEFAULT_PROFILE_URL);
		otherUser = UserFixture.buildActiveUser("other@kakao.com", LoginType.KAKAO, DEFAULT_PROFILE_URL);

		userId = (UUID) ReflectionTestUtils.getField(user, "id");
		otherUserId = (UUID) ReflectionTestUtils.getField(otherUser, "id");
		summaryId = UUID.randomUUID();

		session = Session.builder()
			.user(user)
			.persona(null) // persona는 이 테스트에서 불필요
			.build();
		ReflectionTestUtils.setField(session, "id", UUID.randomUUID());
	}

	// ────────────────────────────────────────────────────────────────────────────
	// 픽스처 헬퍼
	// ────────────────────────────────────────────────────────────────────────────

	private SessionSummary buildSummary(User owner) {
		SessionSummary summary = SessionSummary.builder()
			.fact("오늘 발표를 망쳤다고 느꼈다.")
			.emotion("불안, 자책")
			.insight("실수를 통해 배울 수 있다.")
			.session(session)
			.user(owner)
			.build();
		ReflectionTestUtils.setField(summary, "id", summaryId);
		return summary;
	}

	private EmotionCard buildEmotionCard(Session session, User owner) {
		EmotionCard card = EmotionCard.builder()
			.frontImageUrl("https://s3.example.com/front.jpg")
			.backImageUrl("https://s3.example.com/back.jpg")
			.user(owner)
			.session(session)
			.build();
		ReflectionTestUtils.setField(card, "id", UUID.randomUUID());
		return card;
	}

	// ────────────────────────────────────────────────────────────────────────────
	// uploadSummaryCard()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("uploadSummaryCard()")
	class UploadSummaryCard {

		@Test
		@DisplayName("앞면과 뒷면 이미지를 정상적으로 업로드하면 EmotionCard가 저장된다")
		void validImages_savesEmotionCard() {
			// given
			SessionSummary summary = buildSummary(user);
			MockMultipartFile front = new MockMultipartFile("front", "front.jpg", "image/jpeg", new byte[]{1, 2, 3});
			MockMultipartFile back = new MockMultipartFile("back", "back.jpg", "image/jpeg", new byte[]{4, 5, 6});
			String frontUrl = "https://s3.example.com/front.jpg";
			String backUrl = "https://s3.example.com/back.jpg";

			given(summaryRepository.findByIdOrThrow(summaryId, ErrorCode.NOT_FOUND_SUMMARY)).willReturn(summary);
			given(s3Service.uploadImage(front, S3Path.SUMMARY)).willReturn(frontUrl);
			given(s3Service.uploadImage(back, S3Path.SUMMARY)).willReturn(backUrl);
			given(emotionCardRepository.save(any(EmotionCard.class))).willAnswer(inv -> inv.getArgument(0));

			// when
			summaryService.uploadSummaryCard(userId, summaryId, front, back);

			// then
			then(s3Service).should().uploadImage(front, S3Path.SUMMARY);
			then(s3Service).should().uploadImage(back, S3Path.SUMMARY);
			then(emotionCardRepository).should().save(any(EmotionCard.class));
		}

		@Test
		@DisplayName("앞면 이미지가 null이면 CustomException(INVALID_IMAGE_FILE)을 던진다")
		void nullFrontImage_throwsException() {
			// when & then
			assertThatThrownBy(() -> summaryService.uploadSummaryCard(userId, summaryId, null,
				new MockMultipartFile("back", new byte[]{1})))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.INVALID_IMAGE_FILE);

			then(summaryRepository).should(never()).findByIdOrThrow(any(), any());
		}

		@Test
		@DisplayName("뒷면 이미지가 비어있으면 CustomException(INVALID_IMAGE_FILE)을 던진다")
		void emptyBackImage_throwsException() {
			// given
			MockMultipartFile front = new MockMultipartFile("front", "front.jpg", "image/jpeg", new byte[]{1});
			MockMultipartFile emptyBack = new MockMultipartFile("back", new byte[0]);

			// when & then
			assertThatThrownBy(() -> summaryService.uploadSummaryCard(userId, summaryId, front, emptyBack))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.INVALID_IMAGE_FILE);
		}

		@Test
		@DisplayName("존재하지 않는 summaryId이면 CustomException(NOT_FOUND_SUMMARY)을 던진다")
		void notFoundSummary_throwsException() {
			// given
			MockMultipartFile front = new MockMultipartFile("front", "f.jpg", "image/jpeg", new byte[]{1});
			MockMultipartFile back = new MockMultipartFile("back", "b.jpg", "image/jpeg", new byte[]{2});

			given(summaryRepository.findByIdOrThrow(summaryId, ErrorCode.NOT_FOUND_SUMMARY))
				.willThrow(new CustomException(ErrorCode.NOT_FOUND_SUMMARY));

			// when & then
			assertThatThrownBy(() -> summaryService.uploadSummaryCard(userId, summaryId, front, back))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.NOT_FOUND_SUMMARY);
		}

		@Test
		@DisplayName("summary 소유자가 아닌 userId이면 CustomException(NOT_SUMMARY_USER)을 던진다")
		void notOwner_throwsException() {
			// given
			SessionSummary summary = buildSummary(user); // user 소유
			MockMultipartFile front = new MockMultipartFile("front", "f.jpg", "image/jpeg", new byte[]{1});
			MockMultipartFile back = new MockMultipartFile("back", "b.jpg", "image/jpeg", new byte[]{2});

			given(summaryRepository.findByIdOrThrow(summaryId, ErrorCode.NOT_FOUND_SUMMARY)).willReturn(summary);

			// when & then — otherUser로 요청
			assertThatThrownBy(() -> summaryService.uploadSummaryCard(otherUserId, summaryId, front, back))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.NOT_SUMMARY_USER);
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// getSummaryCard()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("getSummaryCard()")
	class GetSummaryCard {

		@Test
		@DisplayName("정상 요청 시 SummaryCardResponse를 반환한다")
		void validRequest_returnsSummaryCardResponse() {
			// given
			SessionSummary summary = buildSummary(user);
			given(summaryRepository.findByIdOrThrow(summaryId, ErrorCode.NOT_FOUND_SUMMARY)).willReturn(summary);

			// when
			SummaryCardResponse response = summaryService.getSummaryCard(userId, summaryId);

			// then
			assertThat(response).isNotNull();
			assertThat(response.summaryId()).isEqualTo(summaryId);
			assertThat(response.fact()).isEqualTo("오늘 발표를 망쳤다고 느꼈다.");
			assertThat(response.emotion()).isEqualTo("불안, 자책");
			assertThat(response.insight()).isEqualTo("실수를 통해 배울 수 있다.");
			assertThat(response.isEdited()).isFalse();
			assertThat(response.visibility()).isEqualTo("PUBLIC");
		}

		@Test
		@DisplayName("존재하지 않는 summaryId이면 CustomException(NOT_FOUND_SUMMARY)을 던진다")
		void notFoundSummary_throwsException() {
			// given
			given(summaryRepository.findByIdOrThrow(summaryId, ErrorCode.NOT_FOUND_SUMMARY))
				.willThrow(new CustomException(ErrorCode.NOT_FOUND_SUMMARY));

			// when & then
			assertThatThrownBy(() -> summaryService.getSummaryCard(userId, summaryId))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.NOT_FOUND_SUMMARY);
		}

		@Test
		@DisplayName("summary 소유자가 아닌 userId이면 CustomException(NOT_SUMMARY_USER)을 던진다")
		void notOwner_throwsException() {
			// given
			SessionSummary summary = buildSummary(user);
			given(summaryRepository.findByIdOrThrow(summaryId, ErrorCode.NOT_FOUND_SUMMARY)).willReturn(summary);

			// when & then
			assertThatThrownBy(() -> summaryService.getSummaryCard(otherUserId, summaryId))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.NOT_SUMMARY_USER);
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// updateSummaryCard()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("updateSummaryCard()")
	class UpdateSummaryCard {

		@Test
		@DisplayName("정상 요청 시 요약 카드가 수정되고 isEdited=true가 된다")
		void validRequest_updatesSummaryAndSetsIsEditedTrue() {
			// given
			SessionSummary summary = buildSummary(user);
			SummaryCardUpdateRequest request = new SummaryCardUpdateRequest("새로운 사실", "새로운 감정", "새로운 인사이트");

			given(summaryRepository.findByIdOrThrow(summaryId, ErrorCode.NOT_FOUND_SUMMARY)).willReturn(summary);

			// when
			SummaryCardUpdateResponse response = summaryService.updateSummaryCard(userId, summaryId, request);

			// then
			assertThat(response.isEdited()).isTrue();
			assertThat(response.emotion()).isEqualTo("새로운 감정");
			assertThat(response.fact()).isEqualTo("새로운 사실");
			assertThat(response.insight()).isEqualTo("새로운 인사이트");
		}

		@Test
		@DisplayName("null 필드는 기존 값을 유지한다")
		void nullFields_keepOriginalValues() {
			// given
			SessionSummary summary = buildSummary(user);
			SummaryCardUpdateRequest request = new SummaryCardUpdateRequest(null, null, "새로운 인사이트만"); // fact=null, emotion=null, insight=새로운 인사이트만

			given(summaryRepository.findByIdOrThrow(summaryId, ErrorCode.NOT_FOUND_SUMMARY)).willReturn(summary);

			// when
			SummaryCardUpdateResponse response = summaryService.updateSummaryCard(userId, summaryId, request);

			// then
			assertThat(response.emotion()).isEqualTo("불안, 자책");   // 원본 유지
			assertThat(response.fact()).isEqualTo("오늘 발표를 망쳤다고 느꼈다."); // 원본 유지
			assertThat(response.insight()).isEqualTo("새로운 인사이트만");
			assertThat(response.isEdited()).isTrue();
		}

		@Test
		@DisplayName("존재하지 않는 summaryId이면 CustomException(NOT_FOUND_SUMMARY)을 던진다")
		void notFoundSummary_throwsException() {
			// given
			SummaryCardUpdateRequest request = new SummaryCardUpdateRequest("사실", "감정", "인사이트");
			given(summaryRepository.findByIdOrThrow(summaryId, ErrorCode.NOT_FOUND_SUMMARY))
				.willThrow(new CustomException(ErrorCode.NOT_FOUND_SUMMARY));

			// when & then
			assertThatThrownBy(() -> summaryService.updateSummaryCard(userId, summaryId, request))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.NOT_FOUND_SUMMARY);
		}

		@Test
		@DisplayName("summary 소유자가 아닌 userId이면 CustomException(NOT_SUMMARY_USER)을 던진다")
		void notOwner_throwsException() {
			// given
			SessionSummary summary = buildSummary(user);
			SummaryCardUpdateRequest request = new SummaryCardUpdateRequest("사실", "감정", "인사이트");

			given(summaryRepository.findByIdOrThrow(summaryId, ErrorCode.NOT_FOUND_SUMMARY)).willReturn(summary);

			// when & then
			assertThatThrownBy(() -> summaryService.updateSummaryCard(otherUserId, summaryId, request))
				.isInstanceOf(CustomException.class)
				.extracting(e -> ((CustomException) e).getErrorCode())
				.isEqualTo(ErrorCode.NOT_SUMMARY_USER);
		}
	}

	// ────────────────────────────────────────────────────────────────────────────
	// getSummaryCardList()
	// ────────────────────────────────────────────────────────────────────────────

	@Nested
	@DisplayName("getSummaryCardList()")
	class GetSummaryCardList {

		@Test
		@DisplayName("EmotionCard가 있는 summary만 필터링하여 페이지로 반환한다")
		void returnsOnlySummariesWithEmotionCard() {
			// given
			Pageable pageable = PageRequest.of(0, 10);
			SessionSummary summaryWithCard = buildSummary(user);

			UUID anotherSummaryId = UUID.randomUUID();
			SessionSummary summaryWithoutCard = SessionSummary.builder()
				.fact("카드 없는 요약")
				.emotion("평온")
				.insight("없음")
				.session(session)
				.user(user)
				.build();
			ReflectionTestUtils.setField(summaryWithoutCard, "id", anotherSummaryId);

			EmotionCard card = buildEmotionCard(session, user);

			given(summaryRepository.findAllByUserId(userId, pageable))
				.willReturn(new PageImpl<>(List.of(summaryWithCard, summaryWithoutCard)));
			given(emotionCardRepository.findBySessionId(session.getId()))
				.willReturn(java.util.Optional.of(card))  // summaryWithCard → 카드 있음
				.willReturn(java.util.Optional.empty());  // summaryWithoutCard → 카드 없음

			// when
			Page<SummaryCardListResponse> result = summaryService.getSummaryCardList(userId, pageable);

			// then
			assertThat(result.getContent()).hasSize(1);
			assertThat(result.getContent().get(0).summaryId()).isEqualTo(summaryId);
		}

		@Test
		@DisplayName("summary가 없으면 빈 페이지를 반환한다")
		void noSummaries_returnsEmptyPage() {
			// given
			Pageable pageable = PageRequest.of(0, 10);
			given(summaryRepository.findAllByUserId(userId, pageable))
				.willReturn(new PageImpl<>(List.of()));

			// when
			Page<SummaryCardListResponse> result = summaryService.getSummaryCardList(userId, pageable);

			// then
			assertThat(result.getContent()).isEmpty();
		}

		@Test
		@DisplayName("모든 summary에 EmotionCard가 없으면 빈 페이지를 반환한다")
		void allSummariesWithoutCard_returnsEmptyPage() {
			// given
			Pageable pageable = PageRequest.of(0, 10);
			SessionSummary summary = buildSummary(user);

			given(summaryRepository.findAllByUserId(userId, pageable))
				.willReturn(new PageImpl<>(List.of(summary)));
			given(emotionCardRepository.findBySessionId(session.getId()))
				.willReturn(java.util.Optional.empty());

			// when
			Page<SummaryCardListResponse> result = summaryService.getSummaryCardList(userId, pageable);

			// then
			assertThat(result.getContent()).isEmpty();
		}
	}
}