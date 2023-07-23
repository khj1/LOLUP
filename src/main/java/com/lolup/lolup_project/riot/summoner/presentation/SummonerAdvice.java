package com.lolup.lolup_project.riot.summoner.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolup.lolup_project.common.ErrorResponse;

@RestControllerAdvice(assignableTypes = SummonerController.class)
public class SummonerAdvice {

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(WebClientResponseException.class)
	public ResponseEntity<ErrorResponse> webClientResponseHandler(final WebClientResponseException e) {
		ErrorResponse errorResult = new ErrorResponse("404", "해당 소환사가 존재하지 않습니다.");
		return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
	}
}
