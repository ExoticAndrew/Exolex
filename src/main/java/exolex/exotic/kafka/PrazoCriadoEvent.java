package exolex.exotic.kafka;

import java.time.LocalDate;

public record PrazoCriadoEvent(
        Long prazoId,
        Long processoId,
        String descricao,
        LocalDate dataVencimento
) {}