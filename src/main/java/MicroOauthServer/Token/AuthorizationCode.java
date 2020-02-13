package MicroOauthServer.Token;

/**
 * Authorization code used by the Authorization code flow
 * @author etsubu
 */
public class AuthorizationCode {
    private final String code;

    public AuthorizationCode(String code) {
        this.code = code;
    }
}
