package MicroOauthServer.ClientFlow.Flows;

import MicroOauthServer.ClientDatabase.ClientAuthenticationException;
import MicroOauthServer.ClientDatabase.ClientManager;
import MicroOauthServer.ClientDatabase.InvalidScopeException;
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
    public String doFlow(Map<String, String> body, String authorization) throws MicroOauthCoreException, TokenExpiredException, InvalidScopeException {
        String clientId, secret;
        if(authorization == null || !authorization.startsWith("Basic ") || authorization.length() < 7) {
            clientId = body.get("client_id");
            secret = body.get("client_secret");
            log.info("Extracting client credentials from request body");
        } else {
            String[] parts = new String(Base64.getDecoder().decode(authorization.substring(6)), StandardCharsets.UTF_8).split(":");
            if(parts.length != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client credentials were missing");
            }
            clientId = parts[0];
            secret = parts[1];
            log.info("Extracted client credentials from basic auth");
        }
        if(clientId == null || secret == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client credentials were missing");
        }
        try {
            log.info((gson == null) + "----------------" + (manager == null));
            return gson.toJson(manager.authenticateClient(clientId, secret, null));
        } catch (ClientAuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Client id or secret was invalid");
        }
    }
}
