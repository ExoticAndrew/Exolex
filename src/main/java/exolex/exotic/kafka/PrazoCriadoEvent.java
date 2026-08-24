package exolex.exotic.kafka;

import java.time.LocalDate;

public record PrazoCriadoEvent(
        Long prazoId,
        Long processoId,
        String processoNumero,
        String descricao,
        LocalDate dataVencimento,
        Long criadoPorId,
        String criadoPorNome
) {}