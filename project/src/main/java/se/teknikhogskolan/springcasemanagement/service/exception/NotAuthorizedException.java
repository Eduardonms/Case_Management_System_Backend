package se.teknikhogskolan.springcasemanagement.service.exception;

public class NotAuthorizedException extends ServiceException {

    public NotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAuthorizedException(String message) {
        super(message);
    }

}
