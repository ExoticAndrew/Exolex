package exolex.exotic.service;

import exolex.exotic.dtos.AtualizarStatusPrazoDTO;
import exolex.exotic.dtos.PrazoRequestDTO;
import exolex.exotic.dtos.PrazoResponseDTO;
import exolex.exotic.enums.PapelProcesso;
import exolex.exotic.enums.StatusPrazo;
import exolex.exotic.exception.AcessoNegadoException;
import exolex.exotic.exception.PrazoNotFoundException;
import exolex.exotic.exception.ProcessoNotFoundException;
import exolex.exotic.exception.UsuarioNotFoundException;
import exolex.exotic.kafka.PrazoAtualizadoEvent;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário autenticado não encontrado"));
    }

    public PrazoResponseDTO criar(Long processoId, PrazoRequestDTO dto) {
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new ProcessoNotFoundException(processoId));

        Usuario usuarioAtual = getUsuarioAutenticado();
        verificarAcessoEdicao(processoId);

        Prazo prazo = new Prazo();
        prazo.setProcesso(processo);
        prazo.setDescricao(dto.descricao());
        prazo.setDataVencimento(dto.dataVencimento());
        prazoRepository.save(prazo);

        prazoEventProducer.publicarPrazoCriado(new PrazoCriadoEvent(
                prazo.getId(),
                processo.getId(),
                processo.getNumero(),
                prazo.getDescricao(),
                prazo.getDataVencimento(),
                usuarioAtual.getId(),
                usuarioAtual.getNome()
        ));

        return prazoMapper.toResponseDTO(prazo);
    }

    public Page<PrazoResponseDTO> listar(Long processoId, Pageable pageable) {
        verificarAcessoVisualizacao(processoId);
        return prazoRepository.findByProcessoId(processoId, pageable)
                .map(prazoMapper::toResponseDTO);
    }

    public PrazoResponseDTO atualizar(Long processoId, Long prazoId, PrazoRequestDTO dto) {
        verificarAcessoEdicao(processoId);
        Prazo prazo = buscarPrazoDoProcesso(processoId, prazoId);
        Usuario usuarioAtual = getUsuarioAutenticado();

        List<String> camposAlterados = new ArrayList<>();
        if (!Objects.equals(prazo.getDescricao(), dto.descricao())) {
            camposAlterados.add("descricao");
        }
        if (!Objects.equals(prazo.getDataVencimento(), dto.dataVencimento())) {
            camposAlterados.add("dataVencimento");
        }

        prazo.setDescricao(dto.descricao());
        prazo.setDataVencimento(dto.dataVencimento());
        prazoRepository.save(prazo);

        if (!camposAlterados.isEmpty()) {
            publicarAtualizacao(prazo, camposAlterados, usuarioAtual);
        }

        return prazoMapper.toResponseDTO(prazo);
    }

    public PrazoResponseDTO atualizarStatus(Long processoId, Long prazoId, AtualizarStatusPrazoDTO dto) {
        verificarAcessoEdicao(processoId);
        Prazo prazo = buscarPrazoDoProcesso(processoId, prazoId);
        Usuario usuarioAtual = getUsuarioAutenticado();

        StatusPrazo statusAntigo = prazo.getStatus();

        if (!Objects.equals(statusAntigo, dto.status())) {
            prazo.setStatus(dto.status());
            prazoRepository.save(prazo);
            publicarAtualizacao(prazo, List.of("status"), usuarioAtual);
        }

        return prazoMapper.toResponseDTO(prazo);
    }

    public void deletar(Long processoId, Long prazoId) {
        verificarAcessoEdicao(processoId);
        Prazo prazo = buscarPrazoDoProcesso(processoId, prazoId);
        prazoRepository.delete(prazo);
    }

    private void publicarAtualizacao(Prazo prazo, List<String> camposAlterados, Usuario usuarioAtual) {
        prazoEventProducer.publicarPrazoAtualizado(new PrazoAtualizadoEvent(
                prazo.getId(),
                prazo.getProcesso().getId(),
                prazo.getProcesso().getNumero(),
                camposAlterados,
                prazo.getDescricao(),
                prazo.getDataVencimento(),
                prazo.getStatus(),
                usuarioAtual.getId(),
                usuarioAtual.getNome()
        ));
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