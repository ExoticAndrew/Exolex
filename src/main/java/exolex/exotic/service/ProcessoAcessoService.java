package exolex.exotic.service;

import exolex.exotic.enums.PapelProcesso;
import exolex.exotic.exception.AcessoNegadoException;
import exolex.exotic.model.ProcessoUsuario;
import exolex.exotic.model.Usuario;
import exolex.exotic.repository.ProcessoUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessoAcessoService {

    private final ProcessoUsuarioRepository processoUsuarioRepository;

    public ProcessoUsuario buscarVinculo(Long processoId, Usuario usuario) {
        return processoUsuarioRepository.findByProcessoIdAndUsuarioId(processoId, usuario.getId())
                .orElseThrow(() -> new AcessoNegadoException("Você não tem acesso a este processo"));
    }

    public void verificarAcessoVisualizacao(Long processoId, Usuario usuario) {
        buscarVinculo(processoId, usuario);
    }

    public void verificarAcessoEdicao(Long processoId, Usuario usuario) {
        ProcessoUsuario vinculo = buscarVinculo(processoId, usuario);

        if (vinculo.getPapel() == PapelProcesso.VISUALIZADOR) {
            throw new AcessoNegadoException("Visualizadores não podem criar, editar ou excluir recursos deste processo");
        }
    }

    public void exigirPapel(Long processoId, Usuario usuario, PapelProcesso papelExigido, String mensagemErro) {
        ProcessoUsuario vinculo = buscarVinculo(processoId, usuario);

        if (vinculo.getPapel() != papelExigido) {
            throw new AcessoNegadoException(mensagemErro);
        }
    }
}