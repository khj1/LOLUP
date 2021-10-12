package com.lolup.lolup_project.duo;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.lolup.lolup_project.duo.QDuo.duo;
import static com.lolup.lolup_project.member.QMember.member;

@RequiredArgsConstructor
public class DuoRepositoryImpl implements DuoRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;


    @Override
    public Page<DuoDto> findAll(String position, String tier, Pageable pageable) {
        List<Duo> results = queryFactory
                .selectFrom(duo)
                .join(duo.member, member).fetchJoin()
                .where(
                        positionEq(position),
                        tierEq(tier)
                )
                .limit(pageable.getPageSize())
                .fetch();

        List<DuoDto> content = results.stream()
                .map(DuoDto::create)
                .collect(Collectors.toList());

        long total = queryFactory
                .selectFrom(duo)
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression tierEq(String tier) {
        return StringUtils.hasText(tier) ? duo.info.tier.eq(tier) : null;
    }

    private BooleanExpression positionEq(String position) {
        return StringUtils.hasText(position) ? duo.position.eq(position) : null;
    }

    @Override
    public void update(Long duoId, String position, String desc) {
        queryFactory
                .update(duo)
                .set(duo.position, position)
                .set(duo.desc, desc)
                .where(duo.id.eq(duoId))
                .execute();
    }

    /**
     * findById 메서드로 Duo 엔티티 호출 후 findDuo.getMostInfos.clear() 하면
     * orphanremoval = true 속성이 제대로 동작하지 않는 문제 발생
     */
    @Override
    public void delete(Long duoId, Long memberId) {
        queryFactory
                .delete(duo)
                .where(
                        duo.id.eq(duoId),
                        duo.member.id.eq(memberId)
                )
                .execute();
    }
}
