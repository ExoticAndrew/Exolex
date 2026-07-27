package exolex.exotic.dtos;

import exolex.exotic.enums.StatusProcesso;

import java.time.LocalDate;
import java.util.List;

public record ProcessoResponseDTO(
        Long id,
        String numero,
        String titulo,
        StatusProcesso status,
        LocalDate dataAbertura,
        String clienteNome,
        List<VinculoResponseDTO> equipe
) {}