package MicroOauthServer.Clients;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * OauthClient that contains all the client related information to be stored in database
 * @author etsubu
 */
public class OauthClient {
    private final String clientId;
    private final Set<String> scopes;
    private final Set<String> redirectUris;
    private final String secret;
    private final String description;

    /**
     * Initializes OauthClient
     * @param clientId ClientId
     * @param scopes Scopes for the client
     * @param redirectUris All the allowed redirect URIs as regex patterns
     * @param secret Hashed client secret
     * @param description Human readable description of the client
     */
    public OauthClient(String clientId, Set<String> scopes, Set<String> redirectUris, String secret, String description) {
        this.clientId = clientId;
        this.scopes = scopes;
        this.secret = secret;
        this.description = description;
        this.redirectUris = redirectUris;
    }

    public String getClientId() { return clientId; }

    public Set<String> getScopes() { return scopes; }

    public String getSecret() { return secret; }

    public String getDescription() { return description; }

    public Set<String> getRedirectUris() {
        return redirectUris;
    }

    /**
     * Tries to match the given URL to all the available redirect URI regex patterns
     * @param url URL the client request redirect to
     * @return True if the redirect URL matches config and the redirect is allowed
     */
    public boolean matchRedirectUri(String url) {
        return redirectUris.stream().anyMatch(x -> Pattern.matches(x, url));
    }

    /**
     * Only hash the client id because there cannot be multiple clients with same id
     * @return HashCode for client
     */
    @Override
    public int hashCode() {
        return clientId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || o.getClass() != getClass()) {
            return false;
        }
        return clientId.equals(((OauthClient)o).clientId);
    }
}
