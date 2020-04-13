package MicroOauthServer.ClientFlow.Flows;

import MicroOauthServer.ClientDatabase.ClientManager;
import MicroOauthServer.ClientDatabase.InvalidScopeException;
import MicroOauthServer.Exceptions.InvalidClientException;
import MicroOauthServer.Exceptions.MicroOauthCoreException;
import MicroOauthServer.Token.TokenService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Resource Owner Password Credentials Grant, allows client application to obtain an access token with user's
 * credentials. This grant type is deprecated and is disabled by default for the server and for each client.
 * It is implemented to provide compatibility for legacy applications when no other grant type cannot be used.
 * Note that this grant type introduces risks of brute forcing and that the client application has access
 * to user's credentials and thus providing the grant type for OauthClient should be considered carefully and
 * only for highly confidential clients
 * @see [https://tools.ietf.org/html/rfc6749#section-4.3]
 * @author etsubu
 */
@Component
public class ROPCGrant extends GrantFlow {
    private static final Logger log = LoggerFactory.getLogger(ROPCGrant.class);
    public static final String PASSWORD_GRANT_TYPE = "password";
    public static final String RESPONSE_TYPE = "response_type";
    public static final String CLIENT_ID = "client_id";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String STATE = "state";
    public static final String SCOPE = "scope";
    public static final String TOKEN = "token";

    @Autowired
    private ClientManager manager;

    private Gson gson;

    public ROPCGrant() {
        this.gson = new Gson();
    }

    @Override
    public String doFlow(Map<String, String> body, String authorization) throws MicroOauthCoreException, InvalidScopeException, InvalidClientException {
        String[] credentials = super.parseAuthorization(authorization, body);
        String scope = body.get("scope");
        return gson.toJson(manager.authenticateClient(credentials[0], credentials[1], scope));
    }
}
