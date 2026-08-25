package exolex.exotic.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
public class ProcessoEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(ProcessoEventProducer.class);
    private static final String TOPICO = "colaborador-adicionado";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;

    public ProcessoEventProducer(KafkaTemplate<String, String> kafkaTemplate, JsonMapper jsonMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.jsonMapper = jsonMapper;
    }

    public void publicarColaboradorAdicionado(ColaboradorAdicionadoEvent evento) {
        try {
            String payload = jsonMapper.writeValueAsString(evento);
            kafkaTemplate.send(TOPICO, payload);
        } catch (Exception e) {
            logger.error("Erro ao publicar evento ColaboradorAdicionado: {}", e.getMessage(), e);
        }
    }
}