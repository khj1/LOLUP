package com.lolup.lolup_project.duo.domain;

import static com.lolup.lolup_project.duo.domain.QDuo.duo;
import static com.lolup.lolup_project.member.domain.QMember.member;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.lolup.lolup_project.duo.application.dto.DuoDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DuoRepositoryImpl implements DuoRepositoryCustom {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<DuoDto> findAll(final String position, final String tier, final Pageable pageable) {
		List<Duo> results = queryFactory
				.selectFrom(duo)
				.join(duo.member, member).fetchJoin()
				.where(
						positionEq(position),
						tierEq(tier)
				)
				.limit(pageable.getPageSize())
				.orderBy(duo.id.desc())
				.fetch();

		List<DuoDto> content = results.stream()
				.map(DuoDto::create)
				.collect(Collectors.toList());

		long total = queryFactory
				.selectFrom(duo)
				.fetchCount();

		return new PageImpl<>(content, pageable, total);
	}

	private BooleanExpression tierEq(final String tier) {
		return StringUtils.hasText(tier) ? duo.info.tier.eq(tier) : null;
	}

	private BooleanExpression positionEq(final String position) {
		return StringUtils.hasText(position) ? duo.position.eq(position) : null;
	}
}
