package MicroOauthServer.Token;

import java.time.Instant;

public class StorageToken {
    private String clientId;
    private String token;
    private String type;
    private String scopes;
    private long generatedTimestamp;
    private long TTL;

    public StorageToken(String clientId, String token, String type, String scopes, long generatedTimestamp, long TTL) {
        this.clientId = clientId;
        this.token = token;
        this.type = type;
        this.scopes = scopes;
        this.generatedTimestamp = generatedTimestamp;
        this.TTL = TTL;
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
}
