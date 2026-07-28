package exolex.exotic.exception;

import java.time.LocalDateTime;

public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}