package com.lolup.lolup_project.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lolup.lolup_project.auth.application.AuthenticationPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

	private final MemberService memberService;

	@PatchMapping
	public ResponseEntity<Void> updateSummonerName(@AuthenticationPrincipal final Long memberId,
												   @Valid @RequestBody final MemberUpdateRequest request) {
		memberService.updateSummonerName(memberId, request.getSummonerName());
		return ResponseEntity.noContent().build();
	}
}
