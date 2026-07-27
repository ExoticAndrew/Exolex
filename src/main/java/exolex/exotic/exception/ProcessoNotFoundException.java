package exolex.exotic.exception;

public class ProcessoNotFoundException extends RuntimeException {
    public ProcessoNotFoundException(Long id) {
        super("Processo não encontrado com id: " + id);
    }
}