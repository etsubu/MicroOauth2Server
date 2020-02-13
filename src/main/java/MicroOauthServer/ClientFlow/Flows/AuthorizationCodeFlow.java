package MicroOauthServer.ClientFlow.Flows;

import MicroOauthServer.ClientDatabase.InvalidScopeException;
import MicroOauthServer.Exceptions.MicroOauthCoreException;
import MicroOauthServer.Exceptions.TokenExpiredException;
import MicroOauthServer.Token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Implements Authorization Code Flow
 * @author etsubu
 */
public class AuthorizationCodeFlow extends GrantFlow {
    public static final String RESPONSE_TYPE = "response_type";
    public static final String CLIENT_ID = "client_id";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String STATE = "state";
    public static final String SCOPE = "scope";
    public static final String CODE = "code";

    @Autowired
    private TokenService tokenService;

    @Override
    public String doFlow(Map<String, String> body, String authorization) throws MicroOauthCoreException, TokenExpiredException, InvalidScopeException {
        return null;
    }
}
