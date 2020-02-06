package Clients;

import java.util.List;

public class Client {
    private final String clientId;
    private final List<String> scopes;
    private String secret;

    public Client(String clientId, List<String> scopes, String secret) {
        this.clientId = clientId;
        this.scopes = scopes;
        this.secret = secret;
    }

    public String getClientId() { return clientId; }

    public List<String> getScopes() { return scopes; }

    public String getSecret() { return secret; }

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
        return clientId.equals(((Client)o).clientId);
    }
}
