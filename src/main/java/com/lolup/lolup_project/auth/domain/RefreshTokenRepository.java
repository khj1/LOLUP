package com.lolup.lolup_project.auth.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByRefreshToken(final String refreshToken);

	void deleteByRefreshToken(final String refreshToken);
}
