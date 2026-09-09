package exolex.exotic.dtos;

public record LoginResponseDTO(
        Long id,
        String token,
        String nome,
        String fotoUrl
) {}