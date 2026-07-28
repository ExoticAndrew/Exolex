package exolex.exotic.kafka;

import tools.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PrazoEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(PrazoEventProducer.class);
    private static final String TOPICO = "prazo-criado";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper objectMapper;

    public PrazoEventProducer(KafkaTemplate<String, String> kafkaTemplate, JsonMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publicarPrazoCriado(PrazoCriadoEvent evento) {
        try {
            String payload = objectMapper.writeValueAsString(evento);
            kafkaTemplate.send(TOPICO, payload);
        } catch (Exception e) {
            logger.error("Erro ao publicar evento PrazoCriado: {}", e.getMessage(), e);
        }
    }
}