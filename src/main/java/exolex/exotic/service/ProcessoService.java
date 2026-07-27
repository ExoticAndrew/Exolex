package exolex.exotic.service;

import exolex.exotic.dtos.AdicionarColaboradorDTO;
import exolex.exotic.dtos.ProcessoRequestDTO;
import exolex.exotic.dtos.ProcessoResponseDTO;
import exolex.exotic.enums.PapelProcesso;
import exolex.exotic.exception.AcessoNegadoException;
import exolex.exotic.exception.ClienteNotFoundException;
import exolex.exotic.exception.ProcessoNotFoundException;
import exolex.exotic.map.ProcessoMapper;
import exolex.exotic.model.Cliente;
import exolex.exotic.model.Processo;
import exolex.exotic.model.ProcessoUsuario;
import exolex.exotic.model.Usuario;
import exolex.exotic.repository.ClienteRepository;
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
public class ProcessoService {

    private final ProcessoRepository processoRepository;
    private final ProcessoUsuarioRepository processoUsuarioRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProcessoMapper processoMapper;

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
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
        Processo processo = buscarProcessoComAcesso(id);
        return processoMapper.toResponseDTO(processo, processoUsuarioRepository.findByProcesso(processo));
    }

    public void adicionarColaborador(Long processoId, AdicionarColaboradorDTO dto) {
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new ProcessoNotFoundException(processoId));

        exigirPapel(processoId, PapelProcesso.RESPONSAVEL,
                "Apenas o responsável pelo processo pode adicionar colaboradores");

        if (processoUsuarioRepository.existsByProcessoIdAndUsuarioId(processoId, dto.usuarioId())) {
            throw new AcessoNegadoException("Este usuário já está vinculado a este processo");
        }

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ProcessoUsuario vinculo = new ProcessoUsuario();
        vinculo.setProcesso(processo);
        vinculo.setUsuario(usuario);
        vinculo.setPapel(dto.papel());
        processoUsuarioRepository.save(vinculo);
    }

    public void deletar(Long id) {
        exigirPapel(id, PapelProcesso.RESPONSAVEL, "Apenas o responsável pode excluir o processo");
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new ProcessoNotFoundException(id));
        processoRepository.delete(processo);
    }

    private Processo buscarProcessoComAcesso(Long id) {
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new ProcessoNotFoundException(id));

        Usuario usuario = getUsuarioAutenticado();
        processoUsuarioRepository.findByProcessoIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new AcessoNegadoException("Você não tem acesso a este processo"));

        return processo;
    }

    private void exigirPapel(Long processoId, PapelProcesso papelExigido, String mensagemErro) {
        Usuario usuario = getUsuarioAutenticado();
        ProcessoUsuario vinculo = processoUsuarioRepository
                .findByProcessoIdAndUsuarioId(processoId, usuario.getId())
                .orElseThrow(() -> new AcessoNegadoException("Você não tem acesso a este processo"));

        if (vinculo.getPapel() != papelExigido) {
            throw new AcessoNegadoException(mensagemErro);
        }
    }
}