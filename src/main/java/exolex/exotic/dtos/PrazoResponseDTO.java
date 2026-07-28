package exolex.exotic.dtos;

import exolex.exotic.enums.StatusPrazo;

import java.time.LocalDate;

public record PrazoResponseDTO(
        Long id,
        Long processoId,
        String descricao,
        LocalDate dataVencimento,
        StatusPrazo status
) {}