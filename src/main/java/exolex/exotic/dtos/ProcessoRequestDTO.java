package exolex.exotic.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProcessoRequestDTO(
        @NotBlank(message = "Número do processo é obrigatório")
        String numero,

        @NotBlank(message = "Título é obrigatório")
        String titulo,

        @NotNull(message = "Cliente é obrigatório")
        Long clienteId
) {}