package exolex.exotic.controller;

import exolex.exotic.dtos.AdicionarColaboradorDTO;
import exolex.exotic.dtos.ProcessoRequestDTO;
import exolex.exotic.dtos.ProcessoResponseDTO;
import exolex.exotic.service.ProcessoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/processos")
@RequiredArgsConstructor
public class ProcessoController {

    private final ProcessoService processoService;

    @PostMapping
    public ResponseEntity<ProcessoResponseDTO> criar(@Valid @RequestBody ProcessoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(processoService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ProcessoResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "dataAbertura") Pageable pageable) {
        return ResponseEntity.ok(processoService.listarMeusProcessos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(processoService.buscarPorId(id));
    }

    @PostMapping("/{id}/colaboradores")
    public ResponseEntity<Void> adicionarColaborador(
            @PathVariable Long id, @Valid @RequestBody AdicionarColaboradorDTO dto) {
        processoService.adicionarColaborador(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        processoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}