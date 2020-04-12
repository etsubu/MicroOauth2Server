package MicroOauthServer.Authentication;

import MicroOauthServer.Authentication.Exceptions.AuthenticationException;

import java.util.Set;

/**
 * Authentication response containing whether the authentication succeeded, if there is need for MFA, and potential
 * exceptions
 * @author etsubu
 */
public class AuthenticationResponse {

    /**
     * Status of the authentication
     */
    private final boolean authenticated;

    /**
     * Supported MFA methods or empty if there is no need for second authentication step
     */
    private final Set<String> mfaMethods;

    /**
     * Contains potential exception
     */
    private final AuthenticationException exception;

    /**
     * Initializes AuthenticationResponse
     * @param authenticated True if credentials were correct, false if credentials were invalid
     * @param mfaMethods List of supported MFA methods if second authentication step is required
     * @param exception Authentication exception if there were any
     */
    public AuthenticationResponse(boolean authenticated, Set<String> mfaMethods, AuthenticationException exception) {
        this.authenticated = authenticated;
        this.mfaMethods = mfaMethods;
        this.exception = exception;
    }

    /**
     *
     * @return True if credentials were correct
     */
    public boolean isAuthenticated() { return authenticated; }

    /**
     *
     * @return List of supported MFA methods for the required second authentication step
     */
    public Set<String> getMfaMethods() { return mfaMethods; }

    /**
     *
     * @return Authentication exception if there were any
     */
    public AuthenticationException getException() { return exception; }
}
