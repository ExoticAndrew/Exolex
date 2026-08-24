package exolex.exotic.kafka;

import exolex.exotic.enums.StatusPrazo;

import java.time.LocalDate;
import java.util.List;

public record PrazoAtualizadoEvent(
        Long prazoId,
        Long processoId,
        String processoNumero,
        List<String> camposAlterados,
        String descricaoAtual,
        LocalDate dataVencimentoAtual,
        StatusPrazo statusAtual,
        Long alteradoPorId,
        String alteradoPorNome
) {}