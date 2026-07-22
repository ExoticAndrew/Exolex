package exolex.exotic.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TesteConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TesteConsumer.class);

    @KafkaListener(topics = "teste-topic", groupId = "exotic-group")
    public void escutar(String mensagem) {
        logger.info("Mensagem recebida do Kafka: {}", mensagem);
    }
}