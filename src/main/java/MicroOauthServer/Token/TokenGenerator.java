package MicroOauthServer.Token;

import MicroOauthServer.Configuration.Configuration;
import MicroOauthServer.Crypto.SymmetricCrypto;
import MicroOauthServer.Exceptions.TokenExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Component
public class TokenGenerator {
    private Logger log = LoggerFactory.getLogger(TokenGenerator.class);
    public static final String ACCESS_TOKEN_TYPE = "access_token";
    public static final String REFRESH_TOKEN_TYPE = "refresh_token";
    public static final String CODE = "code";
    public static final int ACCESS_TOKEN_LENGTH = 32;
    public static final int AUTHORIZATION_CODE_LENGTH = 16;
    private SecureRandom rand;

    @Autowired
    private SymmetricCrypto crypto;

    @Autowired
    private Configuration configuration;

    public TokenGenerator() {
        rand = new SecureRandom();
    }

    public String generateToken() {
        byte[] buffer = new byte[ACCESS_TOKEN_LENGTH];
        rand.nextBytes(buffer);
        return Base64.getEncoder().encodeToString(buffer);
    }

    public String generateAuthorizationCode() {
        byte[] buffer = new byte[AUTHORIZATION_CODE_LENGTH];
        rand.nextBytes(buffer);
        return Base64.getEncoder().encodeToString(buffer);
    }

    /**
     * Generates new storage token to be saved in token cache
     * @param clientId Client ID the token will refer to
     * @param scopes Scopes this token has associated
     * @param token Actual token value
     * @return StorageToken generated for the client
     */
    public StorageToken generateStorageToken(String clientId, String type, String scopes, String token) {
        Instant time = Instant.now();
        return new StorageToken(clientId, token, type, scopes, time.getEpochSecond(),
                time.getEpochSecond() + configuration.getMicroOauth().getAccessTokenTTL());
    }

    /**
     * Generates new storage token to be saved in token cache
     * @param clientId Client ID the token will refer to
     * @param scopes Scopes this token has associated
     * @param token Actual token value
     * @return StorageToken generated for the client
     */
    public StorageToken generateStorageRefreshToken(String clientId, String type, String scopes, String token) {
        Instant time = Instant.now();
        return new StorageToken(clientId, token, type, scopes, time.getEpochSecond(),
                time.getEpochSecond() + configuration.getMicroOauth().getRefreshTokenTTL());
    }

    public StorageToken generateStorageAuthorizationCode(String clientId, String scopes, String redirectUri, String token) {
        Instant time = Instant.now();
        return new StorageToken(clientId, token, TokenGenerator.CODE, scopes, time.getEpochSecond(),
                time.getEpochSecond() + configuration.getMicroOauth().getAuthorizationCodeTTL(), redirectUri);
    }

    /**
     * Converts Storage MicroOauthServer.Token to a BearerToken to be returned for the client
     * @param token StorageToken to convert
     * @return Valid Bearer MicroOauthServer.Token to be given for the client
     * @throws TokenExpiredException If the given storage token was already expired
     */
    public AuthorizationToken storageTokenToBearerToken(StorageToken token, String refreshToken) throws TokenExpiredException {
        Instant time = Instant.now();
        long TTL = token.getTTL() - time.getEpochSecond();
        if(TTL <= 0) {
            throw new TokenExpiredException();
        }
        // Sign the token
        try {
            String signedTokenKey = Base64.getEncoder().encodeToString(crypto.encrypt(Base64.getDecoder().decode(token.getToken())));
            String signedRefreshToken = Base64.getEncoder().encodeToString(crypto.encrypt(Base64.getDecoder().decode(refreshToken)));
            log.info("Before after signing " + token.getToken() + " : " + signedTokenKey);
            return new AuthorizationToken(signedTokenKey, AuthorizationToken.BEARER_TYPE, TTL, signedRefreshToken, token.getScopes());
        } catch (Exception e) {
            log.error("Failed to encrypt a token ", e);
            throw new TokenExpiredException();
        }
    }
}
