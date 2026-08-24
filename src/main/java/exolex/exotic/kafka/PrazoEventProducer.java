package exolex.exotic.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
public class PrazoEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(PrazoEventProducer.class);
    private static final String TOPICO_CRIADO = "prazo-criado";
    private static final String TOPICO_ATUALIZADO = "prazo-atualizado";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;

    public PrazoEventProducer(KafkaTemplate<String, String> kafkaTemplate, JsonMapper jsonMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.jsonMapper = jsonMapper;
    }

    public void publicarPrazoCriado(PrazoCriadoEvent evento) {
        try {
            String payload = jsonMapper.writeValueAsString(evento);
            kafkaTemplate.send(TOPICO_CRIADO, payload);
        } catch (Exception e) {
            logger.error("Erro ao publicar evento PrazoCriado: {}", e.getMessage(), e);
        }
    }

    public void publicarPrazoAtualizado(PrazoAtualizadoEvent evento) {
        try {
            String payload = jsonMapper.writeValueAsString(evento);
            kafkaTemplate.send(TOPICO_ATUALIZADO, payload);
        } catch (Exception e) {
            logger.error("Erro ao publicar evento PrazoAtualizado: {}", e.getMessage(), e);
        }
    }
}