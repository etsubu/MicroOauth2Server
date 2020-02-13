import MicroOauthServer.ClientDatabase.SqlClientStorageController;
import MicroOauthServer.Clients.OauthClient;
import MicroOauthServer.Configuration.ClientDatabaseConfig;
import MicroOauthServer.Configuration.Configuration;
import MicroOauthServer.Configuration.TokenCacheConfig;
import MicroOauthServer.Token.StorageToken;
import MicroOauthServer.Token.TokenDatabase.SqlTokenCacheController;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

import static junit.framework.TestCase.*;

public class SqlTokenCacheControllerTest {
    @Test
    public void testSqlTokenCache() {
        Configuration config = new Configuration();
        config.setTokenCache(new TokenCacheConfig("sqlite", "jdbc:sqlite::memory:", 3306));
        SqlTokenCacheController controller = new SqlTokenCacheController(config);
        StorageToken token = new StorageToken("testClient", "AAAAAAAA", "access_token", "token-introspect client-mgmt", Long.MAX_VALUE, Long.MAX_VALUE, "http://localhost/");
        // Verify token storing
        assertTrue(controller.addToken(token));
        // Verify querying existing token
        Optional<StorageToken> queriedToken = controller.queryToken(token.getToken());
        assertTrue(queriedToken.isPresent());
        assertEquals("testClient", queriedToken.get().getClientId());
        assertEquals("AAAAAAAA", queriedToken.get().getToken());
        assertEquals("access_token", queriedToken.get().getType());
        assertEquals("token-introspect client-mgmt", queriedToken.get().getScopes());
        assertEquals(Long.MAX_VALUE, queriedToken.get().getGeneratedTimestamp());
        assertEquals(Long.MAX_VALUE, queriedToken.get().getTTL());
        assertEquals("http://localhost/", queriedToken.get().getRedirectUri());

        // Query token which does not exist
        assertTrue(controller.queryToken("BBBBBBBB").isEmpty());

        // Verify token revocation works
        assertTrue(controller.revokeToken(token));
        assertTrue(controller.queryToken(token.getToken()).isEmpty());

        // Verify popping of token works (used by authorization codes which MUST be one time use only)
        controller.addToken(token);
        assertTrue(controller.popToken(token.getToken()).isPresent());
        assertTrue(controller.queryToken(token.getToken()).isEmpty());

        // Verify token revocation for whole client
        controller.addToken(token);
        controller.revokeAllTokensForClient(token.getClientId());
        assertTrue(controller.queryToken(token.getToken()).isEmpty());

        // Test with no redirect uri present
        token = new StorageToken("testClient", "AAAAAAAA", "access_token", "token-introspect client-mgmt", Long.MAX_VALUE, Long.MAX_VALUE);
        assertTrue(controller.addToken(token));
        queriedToken = controller.queryToken(token.getToken());
        assertTrue(queriedToken.isPresent());
        assertEquals("testClient", queriedToken.get().getClientId());
        assertEquals("AAAAAAAA", queriedToken.get().getToken());
        assertEquals("access_token", queriedToken.get().getType());
        assertEquals("token-introspect client-mgmt", queriedToken.get().getScopes());
        assertEquals(Long.MAX_VALUE, queriedToken.get().getGeneratedTimestamp());
        assertEquals(Long.MAX_VALUE, queriedToken.get().getTTL());
        assertNull(queriedToken.get().getRedirectUri());
    }
}
