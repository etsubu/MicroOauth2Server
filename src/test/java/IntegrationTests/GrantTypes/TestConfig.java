package IntegrationTests.GrantTypes;

import MicroOauthServer.ClientDatabase.ClientAuthenticationException;
import MicroOauthServer.ClientDatabase.ClientManager;
import MicroOauthServer.Configuration.ConfigurationService;
import MicroOauthServer.Configuration.MicroOauthConfiguration;
import MicroOauthServer.Sdk.Annotations.Scopes.Scopes;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Set;

/**
 * Test configuration which will set MicroOauth config to use in-memory databases for clients and tokens.
 * Handles initializing those databases to contain required clients for testing
 * @author etsubu
 */
@TestConfiguration
@SpringBootConfiguration
public class TestConfig {

    @Bean("ConfigurationService")
    @Primary
    public ConfigurationService createConfiguration() {
        ConfigurationService config = new ConfigurationService();
        config.getJettyConfig().setPort(80);
        config.getClientDatabase().setControllerName("SQL");
        config.getClientDatabase().setHostName("jdbc:sqlite::memory:");
        config.getTokenCache().setControllerName("SQL");
        config.getTokenCache().setHostname("jdbc:sqlite::memory:");
        /*
        // Bind to port 80
        config.getJettyConfig().setPort(80);
        // Use in memory SQLite database for oauth clients
        config.getClientDatabase().setControllerName("sqlite");
        config.getClientDatabase().setHostName(":memory:");
        // Use in memory SQLite database for token cache
        config.getTokenCache().setControllerName("sqlite");
        config.getTokenCache().setHostname(":memory:");
        System.out.println("config");*/
        return config;
    }

    @Bean
    @Primary
    public ClientManager createClientManager() throws ClientAuthenticationException {
        ClientManager clientManager = new ClientManager();
        return clientManager;
    }
}
