package sanchay.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SanchayFileNotFoundException extends RuntimeException {
    public SanchayFileNotFoundException(String message) {
        super(message);
    }

    public SanchayFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
