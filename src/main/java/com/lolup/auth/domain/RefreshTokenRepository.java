package com.lolup.auth.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByMemberId(final Long memberId);

	Optional<RefreshToken> findByTokenValue(final String tokenValue);

	@Modifying
	@Query("delete from RefreshToken r where r.tokenValue = :tokenValue")
	void deleteByTokenValue(@Param("tokenValue") final String tokenValue);
}
