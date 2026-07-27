package exolex.exotic.map;

import exolex.exotic.dtos.ProcessoResponseDTO;
import exolex.exotic.dtos.VinculoResponseDTO;
import exolex.exotic.model.Processo;
import exolex.exotic.model.ProcessoUsuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessoMapper {

    public ProcessoResponseDTO toResponseDTO(Processo processo, List<ProcessoUsuario> vinculos) {
        List<VinculoResponseDTO> equipe = vinculos.stream()
                .map(v -> new VinculoResponseDTO(
                        v.getUsuario().getId(),
                        v.getUsuario().getNome(),
                        v.getPapel()
                ))
                .collect(Collectors.toList());

        return new ProcessoResponseDTO(
                processo.getId(),
                processo.getNumero(),
                processo.getTitulo(),
                processo.getStatus(),
                processo.getDataAbertura(),
                processo.getCliente().getNome(),
                equipe
        );
    }
}