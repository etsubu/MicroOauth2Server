package MicroOauthServer.Token;

import MicroOauthServer.ClientDatabase.ClientManager;
import MicroOauthServer.ClientDatabase.InvalidScopeException;
import MicroOauthServer.Configuration.ConfigurationService;
import MicroOauthServer.Configuration.MicroOauthConfiguration;
import MicroOauthServer.Crypto.SymmetricCrypto;
import MicroOauthServer.Exceptions.InvalidRedirectUriException;
import MicroOauthServer.Exceptions.MicroOauthCoreException;
import MicroOauthServer.Exceptions.TokenExpiredException;
import MicroOauthServer.Token.TokenDatabase.SimpleTokenStorage;
import MicroOauthServer.Token.TokenDatabase.TokenStorageAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * General level token service which handles creating, querying, revoking of token while also handling verifying of
 * those tokens and thus making token encryption and tamper resistance invisible to the database controller
 */
@Component
public class TokenService {
    private Logger log = LoggerFactory.getLogger(TokenService.class);
    private TokenStorageAPI tokenStorage;

    @Autowired
    private SymmetricCrypto crypto;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private ConfigurationService microOauthConfiguration;

    public TokenService() {
        this.tokenStorage = new SimpleTokenStorage();
    }

    private IntrospectionResponse storageTokenToIntrospectionResponse(StorageToken token) {
        if(!token.isActive())
            return IntrospectionResponse.EXPIRED;
        IntrospectionResponse.Builder builder = new IntrospectionResponse.Builder();
        builder.setActive(token.isActive())
                .setClientId(token.getClientId())
                .setScope(token.getScopes())
                .setType(token.getType());
        return builder.build();
    }

    /**
     * Queries a token information for the given client side Authorization token
     * @param clientToken Raw client side token
     * @return StorageToken associated with the client token
     */
    public Optional<StorageToken> queryToken(String clientToken) {
        byte[] token = Base64.getDecoder().decode(clientToken);
        // Decrypt and validate that the token is not tampered with
        try {
            token = crypto.decrypt(token);
            // MicroOauthServer.Token is now decrypted and can be queried from DB controller
            return tokenStorage.queryToken(Base64.getEncoder().encodeToString(token));
        } catch (Exception e) {
            // MicroOauthServer.Token decryption failed thus it is not created by the server
            log.error("Received invalid token which is not signed by the server");
        }
        return Optional.empty();
    }

    public AuthorizationToken useRefreshToken(String refreshToken, String scopes) throws TokenExpiredException, InvalidScopeException, MicroOauthCoreException {
        StorageToken token = tokenStorage.queryToken(refreshToken)
                .orElseThrow(TokenExpiredException::new);
        if(!token.getToken().equals(TokenGenerator.REFRESH_TOKEN_TYPE)) {
            throw new TokenExpiredException();
        }
        String grantedScopes = ClientManager.validateScopes(token.getScopes(), scopes);
        return generateTokenForClient(token.getClientId(), grantedScopes);
    }

    public AuthorizationToken useAuthorizationCode(String code, String redirectUri) throws TokenExpiredException, InvalidRedirectUriException {
        StorageToken token = tokenStorage.popToken(code)
                .orElseThrow(TokenExpiredException::new);
        log.info("Changing authorization code to token " + code);
        if(!redirectUri.equals(token.getRedirectUri())) {
            throw new InvalidRedirectUriException("Redirect URI was not valid");
        }
        Instant time = Instant.now();
        long TTL = token.getTTL() - time.getEpochSecond();
        if(TTL <= 0) {
            throw new TokenExpiredException();
        }
        log.info("Redirect uris match and code is still active. Generating access_token");
        String rawToken = tokenGenerator.generateToken();
        StorageToken authToken = tokenGenerator.generateStorageToken(token.getClientId(), TokenGenerator.ACCESS_TOKEN_TYPE, token.getScopes(), rawToken);
        log.info("Saving Storage token " + authToken.getToken());
        tokenStorage.addToken(authToken);
        return tokenGenerator.storageTokenToBearerToken(authToken, null);
    }

    public AuthorizationToken generateTokenForClient(String clientId, String scopes) throws MicroOauthCoreException{
        return generateTokenForClient(clientId, scopes, null);
    }

    public AuthorizationCode generateAuthorizationCode(String clientId, String scopes, String redirectUrl) {
        String code = tokenGenerator.generateAuthorizationCode();

        StorageToken token = tokenGenerator.generateStorageAuthorizationCode(clientId, scopes, redirectUrl, code);
        return new AuthorizationCode(token.getToken());
    }

    public AuthorizationToken generateTokenForClient(String clientId, String scopes, String refreshToken) throws MicroOauthCoreException{
        String token = tokenGenerator.generateToken();
        refreshToken = tokenGenerator.generateToken();

        StorageToken storageToken = tokenGenerator.generateStorageToken(clientId, TokenGenerator.ACCESS_TOKEN_TYPE, scopes, token);
        log.info("Storage token " + storageToken.getToken());
        tokenStorage.addToken(storageToken);
        try {
            return tokenGenerator.storageTokenToBearerToken(storageToken, refreshToken);
        } catch (TokenExpiredException e) {
            // This won't happen as we just generated the token
            log.error("We generated a token that is immediately expired ?", e);
            throw new MicroOauthCoreException();
        }
    }

    public IntrospectionResponse introspectToken(String tokenKey) {
        byte[] token = Base64.getDecoder().decode(tokenKey);
        // Decrypt and validate that the token is not tampered with
        try {
            token = crypto.decrypt(token);
            log.info("Introspecting token " + tokenKey + " -> " + Base64.getEncoder().encodeToString(token));
            // MicroOauthServer.Token is now decrypted and can be queried from DB controller
            return tokenStorage.queryToken(Base64.getEncoder().encodeToString(token)).map(this::storageTokenToIntrospectionResponse)
                    .orElse(IntrospectionResponse.EXPIRED);
        } catch (Exception e) {
            // MicroOauthServer.Token decryption failed thus it is not created by the server
            log.error("Received invalid token which is not signed by the server ", e);
            return IntrospectionResponse.EXPIRED;
        }
    }
}
