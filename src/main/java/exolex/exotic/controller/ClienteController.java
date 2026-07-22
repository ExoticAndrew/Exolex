package exolex.exotic.controller;

import exolex.exotic.dtos.ClienteRequestDTO;
import exolex.exotic.dtos.ClienteResponseDTO;
import exolex.exotic.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criar(@Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listar(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(clienteService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.ok(clienteService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}