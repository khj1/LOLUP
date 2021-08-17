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
    public ResponseEntity<List<DuoDto>> findAll(@RequestParam String tier,
                                                @RequestParam String position) {

        List<DuoDto> list = duoService.findAll(tier, position);
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
    public ResponseEntity<Long> update(@PathVariable Long duoId, DuoForm form) {
        return new ResponseEntity<>(duoService.update(duoId, form), HttpStatus.OK);
    }

    @DeleteMapping("/{duoId}")
    public ResponseEntity<Long> delete(@PathVariable Long duoId) {
        return new ResponseEntity<>(duoService.delete(duoId), HttpStatus.OK);
    }
}
