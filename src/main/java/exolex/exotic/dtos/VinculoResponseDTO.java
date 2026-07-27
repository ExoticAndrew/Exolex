package exolex.exotic.dtos;

import exolex.exotic.enums.PapelProcesso;

public record VinculoResponseDTO(
        Long usuarioId,
        String usuarioNome,
        PapelProcesso papel
) {}