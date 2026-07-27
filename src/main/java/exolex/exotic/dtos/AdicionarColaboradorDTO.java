package exolex.exotic.dtos;

import exolex.exotic.enums.PapelProcesso;
import jakarta.validation.constraints.NotNull;

public record AdicionarColaboradorDTO(
        @NotNull(message = "Usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "Papel é obrigatório")
        PapelProcesso papel
) {}