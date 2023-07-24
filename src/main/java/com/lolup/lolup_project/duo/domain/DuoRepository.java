package com.lolup.lolup_project.duo.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DuoRepository extends JpaRepository<Duo, Long>, DuoRepositoryCustom {
	void deleteByIdAndMemberId(final Long duoId, final Long memberId);
}
