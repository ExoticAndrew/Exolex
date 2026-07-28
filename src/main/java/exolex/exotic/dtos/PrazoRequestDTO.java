package exolex.exotic.dtos;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PrazoRequestDTO(
        @NotBlank(message = "Descrição é obrigatória")
        String descricao,

        @NotNull(message = "Data de vencimento é obrigatória")
        @FutureOrPresent(message = "Data de vencimento não pode ser no passado")
        LocalDate dataVencimento
) {}