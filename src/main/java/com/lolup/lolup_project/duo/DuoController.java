package com.lolup.lolup_project.duo;

import java.net.URI;
import java.util.Map;

import org.springframework.data.domain.Pageable;
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
	public ResponseEntity<Map<String, Object>> findAll(String position, String tier, Pageable pageable) {
		Map<String, Object> map = duoService.findAll(position, tier, pageable);
		return new ResponseEntity<>(map, HttpStatus.OK);
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
	public ResponseEntity<Long> delete(@AuthenticationPrincipal Long memberId, @PathVariable Long duoId) {
		return new ResponseEntity<>(duoService.delete(duoId, memberId), HttpStatus.OK);
	}
}
