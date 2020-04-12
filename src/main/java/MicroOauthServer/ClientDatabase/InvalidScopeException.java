package MicroOauthServer.ClientDatabase;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "invalid_scope")
public class InvalidScopeException extends Exception{
}
