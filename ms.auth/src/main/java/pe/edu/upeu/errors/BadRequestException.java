package pe.edu.upeu.errors;

public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(message, 400);
    }
}