package exolex.exotic.controller;

import exolex.exotic.dtos.PrazoProximoDTO;
import exolex.exotic.service.PrazoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prazos")
@RequiredArgsConstructor
public class PrazoProximosController {

    private final PrazoService prazoService;

    @GetMapping("/proximos")
    public ResponseEntity<List<PrazoProximoDTO>> listarProximos() {
        return ResponseEntity.ok(prazoService.listarProximos());
    }
}