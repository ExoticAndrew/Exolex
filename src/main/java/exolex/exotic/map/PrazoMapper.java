package exolex.exotic.map;

import exolex.exotic.dtos.PrazoResponseDTO;
import exolex.exotic.model.Prazo;
import org.springframework.stereotype.Component;

@Component
public class PrazoMapper {

    public PrazoResponseDTO toResponseDTO(Prazo prazo) {
        return new PrazoResponseDTO(
                prazo.getId(),
                prazo.getProcesso().getId(),
                prazo.getDescricao(),
                prazo.getDataVencimento(),
                prazo.getStatus()
        );
    }
}