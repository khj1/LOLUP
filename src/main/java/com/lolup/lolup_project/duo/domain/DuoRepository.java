package com.lolup.lolup_project.duo.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DuoRepository extends JpaRepository<Duo, Long>, DuoRepositoryCustom {

	void deleteByIdAndMemberId(final Long duoId, final Long memberId);

	Optional<Duo> findByIdAndMemberId(final Long duoId, final Long memberId);
}
