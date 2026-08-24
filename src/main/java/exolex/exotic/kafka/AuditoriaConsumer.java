package exolex.exotic.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;

@Component
public class AuditoriaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaConsumer.class);

    private final JsonMapper jsonMapper;

    public AuditoriaConsumer(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @KafkaListener(topics = "prazo-criado", groupId = "auditoria-group")
    public void escutarCriacao(String payload) {
        try {
            PrazoCriadoEvent evento = jsonMapper.readValue(payload, PrazoCriadoEvent.class);
            logger.info("Auditoria: prazo criado - id {}, processo {}, por {}, em {}",
                    evento.prazoId(), evento.processoId(), evento.criadoPorNome(), LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Erro ao processar evento de auditoria (criação): {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "prazo-atualizado", groupId = "auditoria-group")
    public void escutarAtualizacao(String payload) {
        try {
            PrazoAtualizadoEvent evento = jsonMapper.readValue(payload, PrazoAtualizadoEvent.class);
            logger.info("Auditoria: prazo atualizado - id {}, processo {}, campos {}, por {}, em {}",
                    evento.prazoId(), evento.processoId(), evento.camposAlterados(),
                    evento.alteradoPorNome(), LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Erro ao processar evento de auditoria (atualização): {}", e.getMessage(), e);
        }
    }
}