package MicroOauthServer.ClientDatabase;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "invalid_client")
public class ClientAuthenticationException extends Exception {
}
