package MicroOauthServer.Token;

import java.time.Instant;

public class StorageToken {
    private final String clientId;
    private String username;
    private final String token;
    private final String type;
    private final String scopes;
    private final long generatedTimestamp;
    private final long TTL;
    private String redirectUri;

    public StorageToken(String clientId, String token, String type, String scopes, long generatedTimestamp, long TTL) {
        this.clientId = clientId;
        this.token = token;
        this.type = type;
        this.scopes = scopes;
        this.generatedTimestamp = generatedTimestamp;
        this.TTL = TTL;
    }

    public StorageToken(String clientId, String token, String type, String scopes, long generatedTimestamp, long TTL,
                        String redirectUri) {
        this.clientId = clientId;
        this.token = token;
        this.type = type;
        this.scopes = scopes;
        this.generatedTimestamp = generatedTimestamp;
        this.TTL = TTL;
        this.redirectUri = redirectUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getToken() { return token; }

    public String getType() { return type; }

    public String getScopes() { return scopes; }

    public long getGeneratedTimestamp() { return generatedTimestamp; }

    public long getTTL() { return TTL; }

    public boolean isActive() {
        return Instant.now().getEpochSecond() < TTL;
    }

    public String getRedirectUri() { return redirectUri; }

    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
}
