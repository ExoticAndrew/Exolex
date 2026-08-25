package exolex.exotic.exception;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(Long id) {
        super("Usuário não encontrado com id: " + id);
    }

    public UsuarioNotFoundException(String mensagem) {
        super(mensagem);
    }
}