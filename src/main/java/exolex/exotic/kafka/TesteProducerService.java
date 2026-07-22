package exolex.exotic.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TesteProducerService {

    private static final String TOPICO = "teste-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TesteProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviar(String mensagem) {
        kafkaTemplate.send(TOPICO, mensagem);
    }
}