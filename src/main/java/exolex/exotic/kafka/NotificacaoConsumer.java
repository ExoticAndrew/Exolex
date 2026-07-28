package exolex.exotic.kafka;

import tools.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoConsumer.class);

    private final JsonMapper objectMapper;

    public NotificacaoConsumer(JsonMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "prazo-criado", groupId = "notificacao-group")
    public void escutar(String payload) {
        try {
            PrazoCriadoEvent evento = objectMapper.readValue(payload, PrazoCriadoEvent.class);
            logger.info("Notificação: prazo '{}' (id {}) vence em {}, processo {}",
                    evento.descricao(), evento.prazoId(), evento.dataVencimento(), evento.processoId());
        } catch (Exception e) {
            logger.error("Erro ao processar evento de notificação: {}", e.getMessage(), e);
        }
    }
}