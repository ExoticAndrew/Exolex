package exolex.exotic.controller;

import exolex.exotic.dtos.AtualizarStatusPrazoDTO;
import exolex.exotic.dtos.PrazoRequestDTO;
import exolex.exotic.dtos.PrazoResponseDTO;
import exolex.exotic.service.PrazoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/processos/{processoId}/prazos")
@RequiredArgsConstructor
public class PrazoController {

    private final PrazoService prazoService;

    @PostMapping
    public ResponseEntity<PrazoResponseDTO> criar(
            @PathVariable Long processoId, @Valid @RequestBody PrazoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prazoService.criar(processoId, dto));
    }

    @GetMapping
    public ResponseEntity<Page<PrazoResponseDTO>> listar(
            @PathVariable Long processoId,
            @PageableDefault(size = 10, sort = "dataVencimento") Pageable pageable) {
        return ResponseEntity.ok(prazoService.listar(processoId, pageable));
    }

    @PatchMapping("/{prazoId}/status")
    public ResponseEntity<PrazoResponseDTO> atualizarStatus(
            @PathVariable Long processoId, @PathVariable Long prazoId,
            @Valid @RequestBody AtualizarStatusPrazoDTO dto) {
        return ResponseEntity.ok(prazoService.atualizarStatus(processoId, prazoId, dto));
    }

    @DeleteMapping("/{prazoId}")
    public ResponseEntity<Void> deletar(@PathVariable Long processoId, @PathVariable Long prazoId) {
        prazoService.deletar(processoId, prazoId);
        return ResponseEntity.noContent().build();
    }
}