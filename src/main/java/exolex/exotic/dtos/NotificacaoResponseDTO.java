package exolex.exotic.dtos;

import java.time.LocalDateTime;

public record NotificacaoResponseDTO(
        Long id,
        String mensagem,
        boolean lida,
        LocalDateTime criadoEm
) {}