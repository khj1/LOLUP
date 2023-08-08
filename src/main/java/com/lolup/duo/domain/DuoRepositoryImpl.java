package com.lolup.duo.domain;

import static com.lolup.duo.domain.QDuo.duo;
import static com.lolup.member.domain.QMember.member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.lolup.duo.application.dto.DuoDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DuoRepositoryImpl implements DuoRepositoryCustom {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<DuoDto> findAll(final SummonerPosition position, final SummonerTier tier, final Pageable pageable) {
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

		List<DuoDto> content = toDto(results);
		long total = countTotal();

		return new PageImpl<>(content, pageable, total);
	}

	private BooleanExpression positionEq(final SummonerPosition position) {
		return position == null ? null : duo.position.eq(position);
	}

	private BooleanExpression tierEq(final SummonerTier tier) {
		return tier == null ? null : duo.summonerStat.tier.eq(tier);
	}

	private List<DuoDto> toDto(final List<Duo> results) {
		return results.stream()
				.map(DuoDto::create)
				.collect(Collectors.toList());
	}

	private int countTotal() {
		return queryFactory
				.selectFrom(duo)
				.fetch()
				.size();
	}

	@Override
	public Optional<LocalDateTime> findFirstCreatedDateByMemberId(final Long memberId) {
		LocalDateTime createdDate = queryFactory
				.select(duo.createdDate)
				.from(duo)
				.orderBy(duo.createdDate.desc())
				.fetchFirst();

		return Optional.ofNullable(createdDate);
	}
}

