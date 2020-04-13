package MicroOauthServer.ClientFlow.Flows;

import MicroOauthServer.ClientDatabase.InvalidScopeException;
import MicroOauthServer.Exceptions.MicroOauthCoreException;
import MicroOauthServer.Token.AuthorizationToken;
import MicroOauthServer.Exceptions.TokenExpiredException;
import MicroOauthServer.Token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implements ImplicitGrantFlow to be called after Credential Authentication has been validated
 * @author etsubu
 */
@Component
public class ImplicitGrantFlow extends GrantFlow {
    public static final String RESPONSE_TYPE = "response_type";
    public static final String CLIENT_ID = "client_id";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String STATE = "state";
    public static final String SCOPE = "scope";
    public static final String TOKEN = "token";

    @Autowired
    private TokenService tokenService;

    @Override
    public String doFlow(Map<String, String> body, String authorization) throws MicroOauthCoreException, TokenExpiredException, InvalidScopeException {
        String responseType = body.get(RESPONSE_TYPE);
        String clientId = body.get(CLIENT_ID);
        String redirectUri = body.get(REDIRECT_URI);
        String state = body.get(STATE);
        String scope = body.get(SCOPE);
        // State is defined as OPTIONAL in the RFC but due to security reasons we are going to enforce it
        if(responseType == null || clientId == null || redirectUri == null || state == null) {
            throw new MicroOauthCoreException();
        }
        if(!responseType.equals(TOKEN)) {
            throw new MicroOauthCoreException("Response type must be \"token\"");
        }
        AuthorizationToken token = tokenService.generateTokenForClient(clientId, scope, false);
        redirectUri += "?access_token=" + token.getAccess_token() + "&state=" + state + "&expires_in=" + token.getExpires_in();
        return redirectUri;
    }
}
