package exolex.exotic.controller;

import exolex.exotic.dtos.NotificacaoResponseDTO;
import exolex.exotic.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<NotificacaoResponseDTO>> listar() {
        return ResponseEntity.ok(notificacaoService.listar());
    }

    @GetMapping("/nao-lidas")
    public ResponseEntity<Long> contarNaoLidas() {
        return ResponseEntity.ok(notificacaoService.contarNaoLidas());
    }

    @PatchMapping("/{id}/lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id) {
        notificacaoService.marcarComoLida(id);
        return ResponseEntity.noContent().build();
    }
}