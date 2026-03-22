package com.kt.mindLog.repository.session;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kt.mindLog.domain.session.QSession;
import com.kt.mindLog.domain.session.Session;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryCustomImpl implements SessionRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QSession qSession = QSession.session;

	@Override
	public Page<Session> findSessions (
		UUID userId,
		LocalDate startDate,
		LocalDate endDate,
		List<UUID> personaIds,
		Pageable pageable
	) {

		var content = queryFactory
			.selectFrom(qSession)
			.leftJoin(qSession.persona).fetchJoin()
			.where(
				userIdEq(userId),
				startDateGoe(startDate),
				endDateLoe(endDate),
				personaIdsIn(personaIds)
			)
			.orderBy(qSession.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		var total = queryFactory
			.select(qSession.count())
			.from(qSession)
			.where(
				userIdEq(userId),
				startDateGoe(startDate),
				endDateLoe(endDate),
				personaIdsIn(personaIds)
			)
			.fetchOne();

		return new PageImpl<>(content, pageable, total == null ? 0 : total);
	}

	private BooleanExpression userIdEq(UUID userId) {
		return qSession.user.id.eq(userId);
	}

	private BooleanExpression startDateGoe(LocalDate startDate) {
		return startDate != null
			? qSession.startedAt.goe(startDate.atStartOfDay())
			: null;
	}

	private BooleanExpression endDateLoe(LocalDate endDate) {
		return endDate != null
			? qSession.startedAt.lt(endDate.plusDays(1).atStartOfDay())
			: null;
	}

	private BooleanExpression personaIdsIn(List<UUID> personaIds) {
		return (personaIds != null && !personaIds.isEmpty())
			? qSession.persona.id.in(personaIds)
			: null;
	}
}
