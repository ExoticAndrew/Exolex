package exolex.exotic.dtos;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String documento,
        String email,
        String telefone
) {}