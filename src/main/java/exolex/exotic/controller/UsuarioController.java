package exolex.exotic.controller;

import exolex.exotic.dtos.UsuarioResponseDTO;
import exolex.exotic.exception.UsuarioNotFoundException;
import exolex.exotic.model.Usuario;
import exolex.exotic.repository.UsuarioRepository;
import exolex.exotic.service.FotoPerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final FotoPerfilService fotoPerfilService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        List<UsuarioResponseDTO> usuarios = usuarioRepository.findAll().stream()
                .map(u -> new UsuarioResponseDTO(u.getId(), u.getNome(), u.getEmail(), u.getFotoUrl()))
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/me/foto")
    public ResponseEntity<Map<String, String>> atualizarFoto(@RequestParam("arquivo") MultipartFile arquivo) {
        Usuario usuario = getUsuarioAutenticado();
        String url = fotoPerfilService.atualizarFoto(usuario, arquivo);
        return ResponseEntity.ok(Map.of("fotoUrl", url));
    }

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário autenticado não encontrado"));
    }
}