package MicroOauthServer.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The authorization grant type is not supported by the authorization server.
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-5.2"></a>
 * @author etsubu
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "unsupported_grant_type")
public class UnsupportedGrantTypeException extends OauthException{
    private static final String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";

    public UnsupportedGrantTypeException() {
        super(HttpStatus.BAD_REQUEST, UNSUPPORTED_GRANT_TYPE, null, null);
    }

    public UnsupportedGrantTypeException(String description) {
        super(HttpStatus.BAD_REQUEST, UNSUPPORTED_GRANT_TYPE, description, null);
    }
}
