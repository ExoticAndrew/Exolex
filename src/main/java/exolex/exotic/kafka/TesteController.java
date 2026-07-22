package exolex.exotic.kafka;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TesteController {

    private final exolex.exotic.kafka.TesteProducerService producerService;

    public TesteController(exolex.exotic.kafka.TesteProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/api/teste/publicar")
    public String publicar(@RequestParam(defaultValue = "Ola Kafka!") String mensagem) {
        producerService.enviar(mensagem);
        return "Mensagem enviada: " + mensagem;
    }
}