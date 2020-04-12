package MicroOauthServer.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Base configuration class for the oauth server
 * @author etsubu
 */
@Component
public class ConfigurationService {
    private final MicroOauthConfiguration configuration;

    /**
     * Empty constructor for snakeyaml
     */
    public ConfigurationService() {
        configuration = MicroOauthConfiguration.loadConfig(Paths.get("config.yaml"));
    }

    public MicroOauthConfiguration getConfiguration() {
        return configuration;
    }

    public OauthConfig getMicroOauth() { return configuration.getMicroOauth(); }

    public void setMicroOauth(OauthConfig microOauth) { this.configuration.setMicroOauth(microOauth); }

    public JettyConfig getJettyConfig() { return configuration.getJettyConfig(); }

    public void setJettyConfig(JettyConfig jettyConfig) { this.configuration.setJettyConfig(jettyConfig); }

    public ClientDatabaseConfig getClientDatabase() { return configuration.getClientDatabase(); }

    public void setClientDatabase(ClientDatabaseConfig clientDatabase) { this.configuration.setClientDatabase(clientDatabase); }

    public TokenCacheConfig getTokenCache() { return configuration.getTokenCache(); }

    public void setTokenCache(TokenCacheConfig tokenCache) { this.configuration.setTokenCache(tokenCache); }
}
