package se.teknikhogskolan.springcasemanagement.service.exception;

public class HashingException extends ServiceException {

    public HashingException(String message, Exception e) {
        super(message, e);
    }

}
