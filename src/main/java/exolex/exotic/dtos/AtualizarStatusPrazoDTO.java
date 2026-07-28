package exolex.exotic.dtos;

import exolex.exotic.enums.StatusPrazo;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusPrazoDTO(
        @NotNull(message = "Status é obrigatório")
        StatusPrazo status
) {}