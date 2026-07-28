package exolex.exotic.exception;

public class PrazoNotFoundException extends RuntimeException {
    public PrazoNotFoundException(Long id) {
        super("Prazo não encontrado com id: " + id);
    }
}