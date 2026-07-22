package exolex.exotic.exception;

public class ClienteNotFoundException extends RuntimeException {
  public ClienteNotFoundException(String message) {
    super(message);
  }
}
