package MicroOauthServer.Token.TokenDatabase;

import MicroOauthServer.Token.StorageToken;

import java.util.Optional;

public interface TokenStorageAPI {
    Optional<StorageToken> queryToken(String key);
    boolean addToken(StorageToken token);
    boolean revokeToken(StorageToken token);
}
