package exolex.exotic.exception;

public class ClientePossuiProcessosException extends RuntimeException {
    public ClientePossuiProcessosException() {
        super("Não é possível excluir um cliente que possui processos vinculados");
    }
}