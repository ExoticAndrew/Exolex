package exolex.exotic.dtos;

import exolex.exotic.enums.StatusPrazo;

import java.time.LocalDate;

public record PrazoProximoDTO(
        Long id,
        String descricao,
        LocalDate dataVencimento,
        StatusPrazo status,
        Long processoId,
        String processoNumero
) {}