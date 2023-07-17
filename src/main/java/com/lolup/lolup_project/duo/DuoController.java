package com.lolup.lolup_project.duo;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lolup.lolup_project.auth.AuthenticationPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/duo")
public class DuoController {

	private final DuoService duoService;

	@GetMapping
	public ResponseEntity<DuoResponse> findAll(final DuoFindRequest request) {
		DuoResponse duoResponse = duoService.findAll(request.getPosition(), request.getTier(), request.getPageable());
		return new ResponseEntity<>(duoResponse, HttpStatus.OK);
	}

	@PostMapping("/new")
	public ResponseEntity<Void> save(@AuthenticationPrincipal final Long memberId,
									 @RequestBody final DuoSaveRequest request) {
		duoService.save(memberId, request);
		return ResponseEntity.created(URI.create("/duo")).build();
	}

	@PatchMapping("/{duoId}")
	public ResponseEntity<Void> update(@PathVariable final Long duoId,
									   @Valid @RequestBody final DuoUpdateRequest request) {
		duoService.update(duoId, request.getPosition(), request.getDesc());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{duoId}")
	public ResponseEntity<Void> delete(@AuthenticationPrincipal final Long memberId, @PathVariable final Long duoId) {
		duoService.delete(memberId, duoId);
		return ResponseEntity.noContent().build();
	}
}
