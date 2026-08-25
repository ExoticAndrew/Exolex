package exolex.exotic.exception;

public class NotificacaoNotFoundException extends RuntimeException {
    public NotificacaoNotFoundException(Long id) {
        super("Notificação não encontrada com id: " + id);
    }
}