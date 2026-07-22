package exolex.exotic.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 150, message = "Nome deve ter entre 2 e 150 caracteres")
        String nome,

        @NotBlank(message = "Documento (CPF/CNPJ) é obrigatório")
        String documento,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        String telefone
) {}