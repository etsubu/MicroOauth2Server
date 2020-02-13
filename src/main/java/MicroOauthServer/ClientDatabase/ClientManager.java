package MicroOauthServer.ClientDatabase;

import MicroOauthServer.Clients.OauthClient;
import MicroOauthServer.Crypto.PasswordHasherManager;
import MicroOauthServer.Exceptions.MicroOauthCoreException;
import MicroOauthServer.Sdk.Annotations.Scopes.Scopes;
import MicroOauthServer.Token.AuthorizationToken;
import MicroOauthServer.Token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

/**
 * Client manager handles querying, creating, deleting and authenticating oauth2 clients
 * @author etsubu
 */
@Component
public class ClientManager {
    private Logger log = LoggerFactory.getLogger(ClientManager.class);

    @Autowired
    private ClientStorageAPI clientStorage;

    @Autowired
    private PasswordHasherManager hashManager;

    @Autowired
    private TokenService tokenService;

    public ClientManager() {
        log.info("Initialized ClientManager");
    }

    @PostConstruct
    public void init() throws ClientAuthenticationException {
        //createClient("test-client", Set.of("account-mgmt", Scopes.TOKEN_INTROSPECT, "test-scope"), Set.of("http://localhost/healthcheck"), "test-secret", "Test client");
    }

    public static String validateScopes(String availableScopes, String requestedScopes) throws InvalidScopeException{
        if(requestedScopes == null) {
            return availableScopes;
        }
        String[] scopes = requestedScopes.split(" ");
        List<String> availableScopesArray = List.of(availableScopes.split(" "));
        StringBuilder validatedScopes = new StringBuilder(64);
        for(String scope : scopes) {
            if(availableScopesArray.contains(scope)) {
                validatedScopes.append(scope).append(' ');
            } else {
                throw new InvalidScopeException();
            }
        }
        if(validatedScopes.length() > 0) {
            return validatedScopes.deleteCharAt(validatedScopes.length() - 1).toString();
        }
        throw new InvalidScopeException();
    }

    public OauthClient createClient(String clientId, Set<String> scopes, Set<String> redirectUris, String plaintextSecret, String description) throws ClientAuthenticationException {
        if(clientStorage.queryClient(clientId).isEmpty()) {
            String secret = hashManager.hash(plaintextSecret);
            OauthClient client = new OauthClient(clientId, scopes, redirectUris, secret, description);
            clientStorage.addClient(new OauthClient(clientId, scopes, redirectUris, secret, description));
            return client;
        } else {
            throw new ClientAuthenticationException();
        }
    }

    /**
     * Authenticates the client and generates an access token with the requested scopes
     * @param clientId Client ID to authenticate
     * @param secret Client Secret
     * @param requestedScopes Requested scopes for the access token (these must exist in the client's scopes)
     * @return AuthorizationToken with the requested scopes
     * @throws ClientAuthenticationException There was an error with the authentication
     * @throws InvalidScopeException Tries to request a scope that does not belong to the client
     */
    public AuthorizationToken authenticateClient(String clientId, String secret, String requestedScopes) throws ClientAuthenticationException, InvalidScopeException, MicroOauthCoreException {
        OauthClient client = clientStorage.queryClient(clientId)
                .orElseThrow(ClientAuthenticationException::new);
        try {
            if(hashManager.validatePassword(client.getSecret(), secret)) {
                log.info("Successfully authenticated client " + clientId);
                return tokenService.generateTokenForClient(clientId, validateScopes(String.join(" ", client.getScopes()), requestedScopes));
            }
        } catch (IllegalArgumentException e) {
            log.error("Failed to validate password ", e);
        }
        throw new ClientAuthenticationException();
    }

}
