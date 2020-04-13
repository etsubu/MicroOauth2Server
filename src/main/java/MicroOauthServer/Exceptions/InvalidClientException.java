package MicroOauthServer.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "invalid_client")
public class InvalidClientException extends OauthException {
    public static final String INVALID_CLIENT = "invalid_client";
    public static final String INVALID_CLIENT_OR_SECRET = "Invalid client id or secret";
    public static final String CLIENT_SECRET_MISSING = "Client secret was missing";
    public static final String CLIENT_ID_MISSING = "Client id was missing";

    public InvalidClientException(HttpStatus status) {
        super(status, INVALID_CLIENT, null, null);
    }

    public InvalidClientException(HttpStatus status, String description) {
        super(status, INVALID_CLIENT, description, null);
    }

    public InvalidClientException(HttpStatus status, String description, String errorUri) {
        super(status, INVALID_CLIENT, description, errorUri);
    }
}
