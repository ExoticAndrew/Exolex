package exolex.exotic.kafka;

import tools.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditoriaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaConsumer.class);

    private final JsonMapper JsonMapper;

    public AuditoriaConsumer(JsonMapper JsonMapper) {
        this.JsonMapper = JsonMapper;
    }

    @KafkaListener(topics = "prazo-criado", groupId = "auditoria-group")
    public void escutar(String payload) {
        try {
            PrazoCriadoEvent evento = JsonMapper.readValue(payload, PrazoCriadoEvent.class);
            logger.info("Auditoria: prazo criado - id {}, processo {}, em {}",
                    evento.prazoId(), evento.processoId(), LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Erro ao processar evento de auditoria: {}", e.getMessage(), e);
        }
    }
}