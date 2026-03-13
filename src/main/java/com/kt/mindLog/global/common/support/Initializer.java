package com.kt.mindLog.global.common.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kt.mindLog.domain.persona.Persona;
import com.kt.mindLog.repository.PersonaRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Initializer {
	private final PersonaRepository personaRepository;

	@PostConstruct
	@Transactional
	public void init() {
		if (personaRepository.count() > 0) {
			return;
		}

		List<Persona> personas = new ArrayList<>();
		personas.add(Persona.builder()
			.personaType("mental_health")
			.personaName("나무")
			.description("감정 소진, 불안, 우울감 등 정서적 케어와 공감에 집중합니다.")
			.toneSettings("""
				{
					 "persona_name": "나무",
					 "style":    "따뜻하고 공감적인",
					 "focus":    "정서적 공감과 감정 탐색",
					 "traits":   "판단하지 않고 경청하며, 감정을 먼저 수용한다",
					 "approach": "감정 반영 → 정서 탐색 → 안정화 제안",
					 "language": "부드러운 존댓말, 감정 언어 적극 사용",
				}
				""")
			.unlockCredits(0)
			.isDefault(true)
			.build());

		personas.add(Persona.builder()
			.personaType("career_academic")
			.personaName("길")
			.description("목표 설정, 번아웃 관리, 진로 탐색 등 성취와 관련된 고민을 구조화합니다.")
			.toneSettings("""
				{
				     "persona_name": "길",
				     "style":    "구조적이고 목표 지향적인",
				     "focus":    "목표 명료화·번아웃 관리·진로 탐색",
				     "traits":   "고민을 체계적으로 정리하고 구체적인 다음 단계를 함께 설계한다",
				     "approach": "현황 파악 → 목표 구조화 → 실행 계획 수립",
				     "language": "명확하고 건설적인 존댓말",
				}
				""")
			.unlockCredits(0)
			.isDefault(false)
			.build());

		personas.add(Persona.builder()
			.personaType("coaching_psychology")
			.personaName("솔")
			.description("대인관계, 의사소통, 일상적 스트레스 관리 등 실질적인 행동 변화와 솔루션을 제안합니다.")
			.toneSettings("""
				{
				      "persona_name": "솔",
				      "style":    "실용적이고 행동 지향적인",
				      "focus":    "대인관계·의사소통·일상 스트레스 관리",
				      "traits":   "이론보다 실천 가능한 솔루션과 행동 변화에 집중한다",
				      "approach": "문제 명확화 → 패턴 파악 → 구체적 행동 제안",
				      "language": "활기차고 실용적인 존댓말",
				}
				""")
			.unlockCredits(0)
			.isDefault(false)
			.build());

		personaRepository.saveAll(personas);
	}
}
