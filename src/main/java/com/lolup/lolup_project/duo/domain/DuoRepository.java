package com.lolup.lolup_project.duo.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DuoRepository extends JpaRepository<Duo, Long>, DuoRepositoryCustom {
	void deleteByIdAndMemberId(Long duoId, Long memberId);
}
