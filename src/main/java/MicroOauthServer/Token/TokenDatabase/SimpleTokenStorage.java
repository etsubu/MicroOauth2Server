package MicroOauthServer.Token.TokenDatabase;

import MicroOauthServer.Token.StorageToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Simple in memory token storage that does not sync to disk. This is used for testing
 * @author etsubu
 */
public class SimpleTokenStorage implements TokenStorageAPI {
    private final Map<String, StorageToken> tokenCache;

    /**
     * Initializes SimpleTokenStorage
     */
    public SimpleTokenStorage() {
        this.tokenCache = new HashMap<>();
    }

    @Override
    public Optional<StorageToken> queryToken(String key) {
        synchronized (tokenCache) {
            return Optional.ofNullable(tokenCache.get(key));
        }
    }

    @Override
    public boolean addToken(StorageToken token) {
        synchronized (tokenCache) {
            tokenCache.put(token.getToken(), token);
            return true;
        }
    }

    @Override
    public boolean revokeToken(StorageToken token) {
        synchronized (tokenCache) {
            tokenCache.remove(token.getToken());
            return true;
        }
    }
}
