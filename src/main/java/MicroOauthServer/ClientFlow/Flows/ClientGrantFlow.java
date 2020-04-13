package MicroOauthServer.ClientFlow.Flows;

import MicroOauthServer.ClientDatabase.ClientAuthenticationException;
import MicroOauthServer.ClientDatabase.ClientManager;
import MicroOauthServer.ClientDatabase.InvalidScopeException;
import MicroOauthServer.Exceptions.InvalidClientException;
import MicroOauthServer.Exceptions.MicroOauthCoreException;
import MicroOauthServer.Exceptions.TokenExpiredException;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

@Component
public class ClientGrantFlow extends GrantFlow {
    public static final String CLIENT_GRANT_TYPE = "client_credentials";
    private static final Logger log = LoggerFactory.getLogger(ClientGrantFlow.class);

    @Autowired
    private ClientManager manager;

    private Gson gson;

    public ClientGrantFlow() {
        this.gson = new Gson();
        log.info("Initialized ClientGrantFlow");
    }

    @Override
    public String doFlow(Map<String, String> body, String authorization) throws MicroOauthCoreException, InvalidScopeException, InvalidClientException {
        String clientId, secret, scope = body.get("scope");
        if(authorization == null || !authorization.startsWith("Basic ") || authorization.length() < 7) {
            clientId = body.get("client_id");
            secret = body.get("client_secret");
            log.info("Extracting client credentials from request body");
        } else {
            String[] parts = new String(Base64.getDecoder().decode(authorization.substring(6)), StandardCharsets.UTF_8).split(":");
            if(parts.length != 2) {
                throw new InvalidClientException(HttpStatus.BAD_REQUEST, "Invalid authorization header structure", "https://tools.ietf.org/html/rfc2617#section-2");
            }
            clientId = parts[0];
            secret = parts[1];
            log.info("Extracted client credentials from basic auth");
        }
        // Try to give informative error descriptions for the client
        if(clientId == null) {
            throw new InvalidClientException(HttpStatus.BAD_REQUEST, InvalidClientException.CLIENT_ID_MISSING);
        } else if(secret == null) {
            throw new InvalidClientException(HttpStatus.BAD_REQUEST, InvalidClientException.CLIENT_SECRET_MISSING);
        }
        return gson.toJson(manager.authenticateClient(clientId, secret, scope));
    }
}
