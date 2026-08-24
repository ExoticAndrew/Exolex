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

import java.util.ArrayList;
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
    public void escutarCriacao(String payload) {
        try {
            PrazoCriadoEvent evento = jsonMapper.readValue(payload, PrazoCriadoEvent.class);
            String mensagem = evento.criadoPorNome() + " criou o prazo \"" + evento.descricao()
                    + "\" no processo " + evento.processoNumero();

            notificarEquipe(evento.processoId(), evento.criadoPorId(), mensagem);
        } catch (Exception e) {
            logger.error("Erro ao processar evento de criação de prazo: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "prazo-atualizado", groupId = "notificacao-group")
    public void escutarAtualizacao(String payload) {
        try {
            PrazoAtualizadoEvent evento = jsonMapper.readValue(payload, PrazoAtualizadoEvent.class);
            String mensagem = montarMensagemAtualizacao(evento);

            notificarEquipe(evento.processoId(), evento.alteradoPorId(), mensagem);
        } catch (Exception e) {
            logger.error("Erro ao processar evento de atualização de prazo: {}", e.getMessage(), e);
        }
    }

    private void notificarEquipe(Long processoId, Long autorId, String mensagem) {
        List<ProcessoUsuario> equipe = processoUsuarioRepository.findByProcessoId(processoId);

        int notificados = 0;
        for (ProcessoUsuario vinculo : equipe) {
            if (vinculo.getUsuario().getId().equals(autorId)) {
                continue;
            }

            Notificacao notificacao = new Notificacao();
            notificacao.setUsuario(vinculo.getUsuario());
            notificacao.setMensagem(mensagem);
            notificacaoRepository.save(notificacao);
            notificados++;
        }

        logger.info("Notificações criadas: {} usuário(s) — \"{}\"", notificados, mensagem);
    }

    private String montarMensagemAtualizacao(PrazoAtualizadoEvent evento) {
        List<String> partes = new ArrayList<>();

        if (evento.camposAlterados().contains("status")) {
            partes.add("status alterado para " + evento.statusAtual());
        }
        if (evento.camposAlterados().contains("dataVencimento")) {
            partes.add("nova data de vencimento em " + evento.dataVencimentoAtual());
        }
        if (evento.camposAlterados().contains("descricao")) {
            partes.add("descrição atualizada");
        }

        return evento.alteradoPorNome() + " atualizou o prazo \"" + evento.descricaoAtual()
                + "\" no processo " + evento.processoNumero() + ": " + String.join("; ", partes);
    }
}