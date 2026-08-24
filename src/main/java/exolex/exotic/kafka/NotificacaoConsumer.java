package exolex.exotic.kafka;

import exolex.exotic.model.Notificacao;
import exolex.exotic.model.ProcessoUsuario;
import exolex.exotic.repository.NotificacaoRepository;
import exolex.exotic.repository.ProcessoUsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
public class NotificacaoConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoConsumer.class);

    private final JsonMapper jsonMapper;
    private final ProcessoUsuarioRepository processoUsuarioRepository;
    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoConsumer(JsonMapper jsonMapper,
                               ProcessoUsuarioRepository processoUsuarioRepository,
                               NotificacaoRepository notificacaoRepository) {
        this.jsonMapper = jsonMapper;
        this.processoUsuarioRepository = processoUsuarioRepository;
        this.notificacaoRepository = notificacaoRepository;
    }

    @KafkaListener(topics = "prazo-criado", groupId = "notificacao-group")
    public void escutar(String payload) {
        try {
            PrazoCriadoEvent evento = jsonMapper.readValue(payload, PrazoCriadoEvent.class);
            List<ProcessoUsuario> equipe = processoUsuarioRepository.findByProcessoId(evento.processoId());

            String mensagem = evento.criadoPorNome() + " criou o prazo \"" + evento.descricao()
                    + "\" no processo " + evento.processoNumero();

            int notificados = 0;
            for (ProcessoUsuario vinculo : equipe) {
                if (vinculo.getUsuario().getId().equals(evento.criadoPorId())) {
                    continue;
                }

                Notificacao notificacao = new Notificacao();
                notificacao.setUsuario(vinculo.getUsuario());
                notificacao.setMensagem(mensagem);
                notificacaoRepository.save(notificacao);
                notificados++;
            }

            logger.info("Notificações criadas para o prazo {}: {} usuário(s)", evento.prazoId(), notificados);
        } catch (Exception e) {
            logger.error("Erro ao processar evento de notificação: {}", e.getMessage(), e);
        }
    }
}