package exolex.exotic.service;

import exolex.exotic.dtos.AtualizarStatusPrazoDTO;
import exolex.exotic.dtos.PrazoProximoDTO;
import exolex.exotic.dtos.PrazoRequestDTO;
import exolex.exotic.dtos.PrazoResponseDTO;
import exolex.exotic.enums.StatusPrazo;
import exolex.exotic.exception.PrazoNotFoundException;
import exolex.exotic.exception.ProcessoNotFoundException;
import exolex.exotic.exception.UsuarioNotFoundException;
import exolex.exotic.kafka.PrazoAtualizadoEvent;
import exolex.exotic.kafka.PrazoCriadoEvent;
import exolex.exotic.kafka.PrazoEventProducer;
import exolex.exotic.map.PrazoMapper;
import exolex.exotic.model.Prazo;
import exolex.exotic.model.Processo;
import exolex.exotic.model.Usuario;
import exolex.exotic.repository.PrazoRepository;
import exolex.exotic.repository.ProcessoRepository;
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
    private final UsuarioRepository usuarioRepository;
    private final PrazoMapper prazoMapper;
    private final PrazoEventProducer prazoEventProducer;
    private final ProcessoAcessoService processoAcessoService;

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário autenticado não encontrado"));
    }

    public PrazoResponseDTO criar(Long processoId, PrazoRequestDTO dto) {
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new ProcessoNotFoundException(processoId));

        Usuario usuarioAtual = getUsuarioAutenticado();
        processoAcessoService.verificarAcessoEdicao(processoId, usuarioAtual);

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
        processoAcessoService.verificarAcessoVisualizacao(processoId, getUsuarioAutenticado());
        return prazoRepository.findByProcessoId(processoId, pageable)
                .map(prazoMapper::toResponseDTO);
    }

    public List<PrazoProximoDTO> listarProximos() {
        Usuario usuario = getUsuarioAutenticado();
        List<Processo> processosVinculados = processoRepository.findByUsuarioVinculado(usuario);

        if (processosVinculados.isEmpty()) {
            return List.of();
        }

        List<Long> processoIds = processosVinculados.stream().map(Processo::getId).toList();

        return prazoRepository
                .findTop10ByProcessoIdInAndStatusNotOrderByDataVencimentoAsc(processoIds, StatusPrazo.CUMPRIDO)
                .stream()
                .map(p -> new PrazoProximoDTO(
                        p.getId(),
                        p.getDescricao(),
                        p.getDataVencimento(),
                        p.getStatus(),
                        p.getProcesso().getId(),
                        p.getProcesso().getNumero()
                ))
                .toList();
    }

    public PrazoResponseDTO atualizar(Long processoId, Long prazoId, PrazoRequestDTO dto) {
        Usuario usuarioAtual = getUsuarioAutenticado();
        processoAcessoService.verificarAcessoEdicao(processoId, usuarioAtual);
        Prazo prazo = buscarPrazoDoProcesso(processoId, prazoId);

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
            publicarAtualizacao(prazo, camposAlterados, usuarioAtual.getId(), usuarioAtual.getNome());
        }

        return prazoMapper.toResponseDTO(prazo);
    }

    public PrazoResponseDTO atualizarStatus(Long processoId, Long prazoId, AtualizarStatusPrazoDTO dto) {
        Usuario usuarioAtual = getUsuarioAutenticado();
        processoAcessoService.verificarAcessoEdicao(processoId, usuarioAtual);
        Prazo prazo = buscarPrazoDoProcesso(processoId, prazoId);

        StatusPrazo statusAntigo = prazo.getStatus();

        if (!Objects.equals(statusAntigo, dto.status())) {
            prazo.setStatus(dto.status());
            prazoRepository.save(prazo);
            publicarAtualizacao(prazo, List.of("status"), usuarioAtual.getId(), usuarioAtual.getNome());
        }

        return prazoMapper.toResponseDTO(prazo);
    }

    public void atualizarStatusPorSistema(Long processoId, Long prazoId, AtualizarStatusPrazoDTO dto) {
        Prazo prazo = buscarPrazoDoProcesso(processoId, prazoId);
        StatusPrazo statusAntigo = prazo.getStatus();

        if (!Objects.equals(statusAntigo, dto.status())) {
            prazo.setStatus(dto.status());
            prazoRepository.save(prazo);
            publicarAtualizacao(prazo, List.of("status"), null, "Sistema (vencimento automático)");
        }
    }

    public void deletar(Long processoId, Long prazoId) {
        processoAcessoService.verificarAcessoEdicao(processoId, getUsuarioAutenticado());
        Prazo prazo = buscarPrazoDoProcesso(processoId, prazoId);
        prazoRepository.delete(prazo);
    }

    private void publicarAtualizacao(Prazo prazo, List<String> camposAlterados, Long autorId, String autorNome) {
        prazoEventProducer.publicarPrazoAtualizado(new PrazoAtualizadoEvent(
                prazo.getId(),
                prazo.getProcesso().getId(),
                prazo.getProcesso().getNumero(),
                camposAlterados,
                prazo.getDescricao(),
                prazo.getDataVencimento(),
                prazo.getStatus(),
                autorId,
                autorNome
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
}