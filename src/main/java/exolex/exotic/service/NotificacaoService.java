package exolex.exotic.service;

import exolex.exotic.dtos.NotificacaoResponseDTO;
import exolex.exotic.model.Notificacao;
import exolex.exotic.model.Usuario;
import exolex.exotic.repository.NotificacaoRepository;
import exolex.exotic.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public List<NotificacaoResponseDTO> listar() {
        return notificacaoRepository.findByUsuarioOrderByCriadoEmDesc(getUsuarioAutenticado()).stream()
                .map(n -> new NotificacaoResponseDTO(n.getId(), n.getMensagem(), n.isLida(), n.getCriadoEm()))
                .toList();
    }

    public long contarNaoLidas() {
        return notificacaoRepository.countByUsuarioAndLidaFalse(getUsuarioAutenticado());
    }

    public void marcarComoLida(Long id) {
        Usuario usuario = getUsuarioAutenticado();

        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        if (!notificacao.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Você não tem permissão para acessar esta notificação");
        }

        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }
}