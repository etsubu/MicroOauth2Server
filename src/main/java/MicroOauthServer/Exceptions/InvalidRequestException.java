package MicroOauthServer.Exceptions;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends OauthException {
    public static final String INVALID_REQUEST = "invalid_request";

    public InvalidRequestException() {
        super(HttpStatus.BAD_REQUEST, INVALID_REQUEST, null, null);
    }

    public InvalidRequestException(String description) {
        super(HttpStatus.BAD_REQUEST, INVALID_REQUEST, description, null);
    }

    public InvalidRequestException(String description, String errorUri) {
        super(HttpStatus.BAD_REQUEST, INVALID_REQUEST, description, errorUri);
    }
}
