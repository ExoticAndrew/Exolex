package exolex.exotic.service;

import exolex.exotic.dtos.AtualizarStatusPrazoDTO;
import exolex.exotic.enums.StatusPrazo;
import exolex.exotic.model.Prazo;
import exolex.exotic.repository.PrazoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PrazoVencimentoScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PrazoVencimentoScheduler.class);

    private final PrazoRepository prazoRepository;
    private final PrazoService prazoService;

    @Scheduled(cron = "0 0 * * * *")
    public void marcarPrazosVencidos() {
        List<Prazo> pendentesVencidos = prazoRepository
                .findByStatusAndDataVencimentoBefore(StatusPrazo.PENDENTE, LocalDate.now());

        if (pendentesVencidos.isEmpty()) {
            return;
        }

        logger.info("Job de vencimento: {} prazo(s) para marcar como VENCIDO", pendentesVencidos.size());

        for (Prazo prazo : pendentesVencidos) {
            try {
                prazoService.atualizarStatusPorSistema(
                        prazo.getProcesso().getId(),
                        prazo.getId(),
                        new AtualizarStatusPrazoDTO(StatusPrazo.VENCIDO)
                );
            } catch (Exception e) {
                logger.error("Erro ao marcar prazo {} como vencido: {}", prazo.getId(), e.getMessage(), e);
            }
        }
    }
}