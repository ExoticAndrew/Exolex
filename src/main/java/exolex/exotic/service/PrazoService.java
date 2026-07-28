package exolex.exotic.service;

import exolex.exotic.dtos.AtualizarStatusPrazoDTO;
import exolex.exotic.dtos.PrazoRequestDTO;
import exolex.exotic.dtos.PrazoResponseDTO;
import exolex.exotic.enums.PapelProcesso;
import exolex.exotic.exception.AcessoNegadoException;
import exolex.exotic.exception.PrazoNotFoundException;
import exolex.exotic.exception.ProcessoNotFoundException;
import exolex.exotic.kafka.PrazoCriadoEvent;
import exolex.exotic.kafka.PrazoEventProducer;
import exolex.exotic.map.PrazoMapper;
import exolex.exotic.model.Prazo;
import exolex.exotic.model.Processo;
import exolex.exotic.model.ProcessoUsuario;
import exolex.exotic.model.Usuario;
import exolex.exotic.repository.PrazoRepository;
import exolex.exotic.repository.ProcessoRepository;
import exolex.exotic.repository.ProcessoUsuarioRepository;
import exolex.exotic.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrazoService {

    private final PrazoRepository prazoRepository;
    private final ProcessoRepository processoRepository;
    private final ProcessoUsuarioRepository processoUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrazoMapper prazoMapper;
    private final PrazoEventProducer prazoEventProducer;

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public PrazoResponseDTO criar(Long processoId, PrazoRequestDTO dto) {
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new ProcessoNotFoundException(processoId));

        verificarAcessoEdicao(processoId);

        Prazo prazo = new Prazo();
        prazo.setProcesso(processo);
        prazo.setDescricao(dto.descricao());
        prazo.setDataVencimento(dto.dataVencimento());
        prazoRepository.save(prazo);

        prazoEventProducer.publicarPrazoCriado(new PrazoCriadoEvent(
                prazo.getId(), processo.getId(), prazo.getDescricao(), prazo.getDataVencimento()
        ));

        return prazoMapper.toResponseDTO(prazo);
    }

    public Page<PrazoResponseDTO> listar(Long processoId, Pageable pageable) {
        verificarAcessoVisualizacao(processoId);
        return prazoRepository.findByProcessoId(processoId, pageable)
                .map(prazoMapper::toResponseDTO);
    }

    public PrazoResponseDTO atualizarStatus(Long processoId, Long prazoId, AtualizarStatusPrazoDTO dto) {
        verificarAcessoEdicao(processoId);
        Prazo prazo = buscarPrazoDoProcesso(processoId, prazoId);
        prazo.setStatus(dto.status());
        return prazoMapper.toResponseDTO(prazoRepository.save(prazo));
    }

    public void deletar(Long processoId, Long prazoId) {
        verificarAcessoEdicao(processoId);
        Prazo prazo = buscarPrazoDoProcesso(processoId, prazoId);
        prazoRepository.delete(prazo);
    }

    private Prazo buscarPrazoDoProcesso(Long processoId, Long prazoId) {
        Prazo prazo = prazoRepository.findById(prazoId)
                .orElseThrow(() -> new PrazoNotFoundException(prazoId));

        if (!prazo.getProcesso().getId().equals(processoId)) {
            throw new PrazoNotFoundException(prazoId);
        }
        return prazo;
    }

    private void verificarAcessoVisualizacao(Long processoId) {
        Usuario usuario = getUsuarioAutenticado();
        processoUsuarioRepository.findByProcessoIdAndUsuarioId(processoId, usuario.getId())
                .orElseThrow(() -> new AcessoNegadoException("Você não tem acesso a este processo"));
    }

    private void verificarAcessoEdicao(Long processoId) {
        Usuario usuario = getUsuarioAutenticado();
        ProcessoUsuario vinculo = processoUsuarioRepository
                .findByProcessoIdAndUsuarioId(processoId, usuario.getId())
                .orElseThrow(() -> new AcessoNegadoException("Você não tem acesso a este processo"));

        if (vinculo.getPapel() == PapelProcesso.VISUALIZADOR) {
            throw new AcessoNegadoException("Visualizadores não podem criar, editar ou excluir prazos");
        }
    }
}