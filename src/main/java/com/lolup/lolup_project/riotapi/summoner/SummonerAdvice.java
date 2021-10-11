package com.lolup.lolup_project.riotapi.summoner;

import com.lolup.lolup_project.exception.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice(assignableTypes = SummonerController.class)
public class SummonerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResult> webClientResponseHandler(WebClientResponseException e) {
        ErrorResult errorResult = new ErrorResult("404", "해당 소환사가 존재하지 않습니다.");
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }
}
