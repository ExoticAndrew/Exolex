package exolex.exotic.kafka;

import exolex.exotic.enums.PapelProcesso;

public record ColaboradorAdicionadoEvent(
        Long processoId,
        String processoNumero,
        Long usuarioAdicionadoId,
        PapelProcesso papel,
        String adicionadoPorNome
) {}