package com.lolup.config;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "localDateTimeProvider")
public class JpaAuditingConfig {

	@Bean
	public DateTimeProvider localDateTimeProvider() {
		return new LocalDateTimeProvider();
	}

	private static class LocalDateTimeProvider implements DateTimeProvider {

		@Override
		public Optional<TemporalAccessor> getNow() {
			return Optional.of(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
		}
	}
}
