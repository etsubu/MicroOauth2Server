package MicroOauthServer.Clients;

import java.util.List;

public class OauthClient {
    private final String clientId;
    private final List<String> scopes;
    private final String secret;
    private final String description;

    public OauthClient(String clientId, List<String> scopes, String secret, String description) {
        this.clientId = clientId;
        this.scopes = scopes;
        this.secret = secret;
        this.description = description;
    }

    public String getClientId() { return clientId; }

    public List<String> getScopes() { return scopes; }

    public String getSecret() { return secret; }

    public String getDescription() { return description; }

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
