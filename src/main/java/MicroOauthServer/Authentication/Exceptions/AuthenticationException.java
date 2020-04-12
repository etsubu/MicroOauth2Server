package MicroOauthServer.Authentication.Exceptions;

/**
 * Base exception for authentication errors
 * @author etsubu
 */
public abstract class AuthenticationException extends Exception {
    private final String code;

    public AuthenticationException(String code) {
        super();
        this.code = code;
    }

    public AuthenticationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() { return code; }
}
