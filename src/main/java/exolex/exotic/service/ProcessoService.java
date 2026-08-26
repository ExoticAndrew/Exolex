package exolex.exotic.service;

import exolex.exotic.dtos.AdicionarColaboradorDTO;
import exolex.exotic.dtos.ProcessoRequestDTO;
import exolex.exotic.dtos.ProcessoResponseDTO;
import exolex.exotic.enums.PapelProcesso;
import exolex.exotic.exception.AcessoNegadoException;
import exolex.exotic.exception.ClienteNotFoundException;
import exolex.exotic.exception.ProcessoNotFoundException;
import exolex.exotic.exception.UsuarioNotFoundException;
import exolex.exotic.kafka.ColaboradorAdicionadoEvent;
import exolex.exotic.kafka.ProcessoEventProducer;
import exolex.exotic.map.ProcessoMapper;
import exolex.exotic.model.Cliente;
import exolex.exotic.model.Processo;
import exolex.exotic.model.ProcessoUsuario;
import exolex.exotic.model.Usuario;
import exolex.exotic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessoService {

    private final ProcessoRepository processoRepository;
    private final ProcessoUsuarioRepository processoUsuarioRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProcessoMapper processoMapper;
    private final ProcessoEventProducer processoEventProducer;
    private final PrazoRepository prazoRepository;
    private final ProcessoAcessoService processoAcessoService;

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário autenticado não encontrado"));
    }

    public ProcessoResponseDTO criar(ProcessoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new ClienteNotFoundException(dto.clienteId()));

        Processo processo = new Processo();
        processo.setNumero(dto.numero());
        processo.setTitulo(dto.titulo());
        processo.setCliente(cliente);
        processoRepository.save(processo);

        ProcessoUsuario vinculo = new ProcessoUsuario();
        vinculo.setProcesso(processo);
        vinculo.setUsuario(getUsuarioAutenticado());
        vinculo.setPapel(PapelProcesso.RESPONSAVEL);
        processoUsuarioRepository.save(vinculo);

        return processoMapper.toResponseDTO(processo, processoUsuarioRepository.findByProcesso(processo));
    }

    public Page<ProcessoResponseDTO> listarMeusProcessos(Pageable pageable) {
        return processoRepository.findByUsuarioVinculado(getUsuarioAutenticado(), pageable)
                .map(processo -> processoMapper.toResponseDTO(
                        processo, processoUsuarioRepository.findByProcesso(processo)));
    }

    public ProcessoResponseDTO buscarPorId(Long id) {
        Usuario usuario = getUsuarioAutenticado();
        processoAcessoService.verificarAcessoVisualizacao(id, usuario);

        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new ProcessoNotFoundException(id));

        return processoMapper.toResponseDTO(processo, processoUsuarioRepository.findByProcesso(processo));
    }

    public void adicionarColaborador(Long processoId, AdicionarColaboradorDTO dto) {
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new ProcessoNotFoundException(processoId));

        Usuario usuarioAtual = getUsuarioAutenticado();
        processoAcessoService.exigirPapel(processoId, usuarioAtual, PapelProcesso.RESPONSAVEL,
                "Apenas o responsável pelo processo pode adicionar colaboradores");

        if (processoUsuarioRepository.existsByProcessoIdAndUsuarioId(processoId, dto.usuarioId())) {
            throw new AcessoNegadoException("Este usuário já está vinculado a este processo");
        }

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException(dto.usuarioId()));

        ProcessoUsuario vinculo = new ProcessoUsuario();
        vinculo.setProcesso(processo);
        vinculo.setUsuario(usuario);
        vinculo.setPapel(dto.papel());
        processoUsuarioRepository.save(vinculo);

        processoEventProducer.publicarColaboradorAdicionado(new ColaboradorAdicionadoEvent(
                processo.getId(),
                processo.getNumero(),
                usuario.getId(),
                dto.papel(),
                usuarioAtual.getNome()
        ));
    }

    public void deletar(Long id) {
        Usuario usuario = getUsuarioAutenticado();
        processoAcessoService.exigirPapel(id, usuario, PapelProcesso.RESPONSAVEL,
                "Apenas o responsável pode excluir o processo");

        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new ProcessoNotFoundException(id));

        prazoRepository.deleteAll(prazoRepository.findByProcessoId(id));
        processoUsuarioRepository.deleteAll(processoUsuarioRepository.findByProcesso(processo));
        processoRepository.delete(processo);
    }
}