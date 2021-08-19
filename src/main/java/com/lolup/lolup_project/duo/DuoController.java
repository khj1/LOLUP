package com.lolup.lolup_project.duo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/duo")
public class DuoController {

    private final DuoService duoService;

    @GetMapping
    public ResponseEntity<List<DuoDto>> findAll(String position, String tier) {
        List<DuoDto> list = duoService.findAll(position, tier);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{duoId}")
    public ResponseEntity<DuoDto> findById(@PathVariable Long duoId) {
        DuoDto dto = duoService.findById(duoId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Long> save(DuoForm form) {
        return new ResponseEntity<>(duoService.save(form), HttpStatus.OK);
    }

    @PatchMapping("/{duoId}")
    public ResponseEntity<Long> update(@PathVariable Long duoId, String position, String desc) {
        return new ResponseEntity<>(duoService.update(duoId, position, desc), HttpStatus.OK);
    }

    @DeleteMapping("/{duoId}")
    public ResponseEntity<Long> delete(@PathVariable Long duoId) {
        return new ResponseEntity<>(duoService.delete(duoId), HttpStatus.OK);
    }
}
