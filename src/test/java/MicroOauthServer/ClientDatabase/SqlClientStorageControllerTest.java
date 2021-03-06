package MicroOauthServer.ClientDatabase;

import MicroOauthServer.ClientDatabase.SqlClientStorageController;
import MicroOauthServer.Clients.OauthClient;
import MicroOauthServer.Configuration.ClientDatabaseConfig;
import MicroOauthServer.Configuration.ConfigurationService;
import MicroOauthServer.Configuration.MicroOauthConfiguration;
import org.junit.Test;
import org.testng.annotations.Ignore;

import java.util.Optional;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class SqlClientStorageControllerTest {


    @Test
    public void testSQLDatabase() {
        ConfigurationService config = new ConfigurationService();
        config.setClientDatabase(new ClientDatabaseConfig("SQL", "jdbc:sqlite::memory:", 3306));
        SqlClientStorageController controller = new SqlClientStorageController(config);
        OauthClient client = new OauthClient("testClient", Set.of("token-introspect", "client-mgmt"), Set.of("http://localhost/", "http://example.com/"), "TestSecret","This is a test client");
        assertTrue(controller.addClient(client));

        Optional<OauthClient> response = controller.queryClient("testClient");
        assertTrue(response.isPresent());
        assertEquals(client.getClientId(), response.get().getClientId());
        assertEquals(client.getScopes(), response.get().getScopes());
        assertEquals(client.getDescription(), response.get().getDescription());
        assertTrue(response.get().getRedirectUris().contains("http://localhost/") && response.get().getRedirectUris().contains("http://example.com/"));

        assertTrue(controller.removeClient("testClient"));
        assertTrue(controller.queryClient("testClient").isEmpty());
    }
}
