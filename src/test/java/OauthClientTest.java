import MicroOauthServer.Clients.OauthClient;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests that the Oauth redirect URL verification works properly to avoid malicious redirects
 * @author etsubu
 */
public class OauthClientTest {

    @Test
    public void testUnauthorizedRedirectUris() {
        Set<String> redirectUris = new HashSet<>();
        redirectUris.add("http://localhost/");
        assertFalse(new OauthClient("",null, redirectUris, null, null).matchRedirectUri("http://example.com/"));
        assertFalse(new OauthClient("",null, redirectUris, null, null).matchRedirectUri("http://localhost/subdomain/"));
        assertFalse(new OauthClient("",null, redirectUris, null, null).matchRedirectUri("http://localhost"));
    }

    @Test
    public void testAauthorizedRedirectUris() {
        Set<String> redirectUris = new HashSet<>();
        redirectUris.add("http://localhost/");
        assertTrue(new OauthClient("",null, redirectUris, null, null).matchRedirectUri("http://localhost/"));
        redirectUris.clear();
        redirectUris.add("http://localhost/.*");
        assertTrue(new OauthClient("",null, redirectUris, null, null).matchRedirectUri("http://localhost/somesubpath"));
        assertTrue(new OauthClient("",null, redirectUris, null, null).matchRedirectUri("http://localhost/somesubpath/more"));
        redirectUris.add("^http://example.com/");
        assertTrue(new OauthClient("",null, redirectUris, null, null).matchRedirectUri("http://example.com/"));
    }
}
