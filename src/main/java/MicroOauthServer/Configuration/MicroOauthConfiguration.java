package MicroOauthServer.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

/**
 * Base configuration class for the oauth server
 * @author etsubu
 */
public class MicroOauthConfiguration {
    private static final Logger log = LoggerFactory.getLogger(MicroOauthConfiguration.class);
    private OauthConfig microOauth;
    private JettyConfig jettyConfig;
    private ClientDatabaseConfig clientDatabase;
    private TokenCacheConfig tokenCache;

    /**
     * Empty constructor for snakeyaml
     */
    public MicroOauthConfiguration() {

    }

    public static MicroOauthConfiguration loadConfig(Path path) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            MicroOauthConfiguration config = mapper.readValue(path.toFile(), MicroOauthConfiguration.class);
            mapper.findAndRegisterModules();
            return config;
        } catch (IOException e) {
            /*if(Files.exists(Paths.get("config.yaml"))) {
                log.error("Configuration file could not be deserialized ", e);
                System.exit(0);
                throw new RuntimeException("Invalid configuration");
            }*/
        }
        MicroOauthConfiguration config = MicroOauthConfiguration.generateDefaultConfig();
        try{
            mapper.writeValue(new File("config.yaml"), config);
        } catch (IOException e) {
            log.error("Failed to save default configuration to a file ", e);
        }
        return config;
    }

    public MicroOauthConfiguration(MicroOauthConfiguration c) {
        this.microOauth = c.getMicroOauth();
        this.jettyConfig = c.getJettyConfig();
        this.clientDatabase = c.getClientDatabase();
        this.tokenCache = c.getTokenCache();
    }

    public MicroOauthConfiguration(OauthConfig microOauth, JettyConfig jettyConfig, ClientDatabaseConfig clientDatabase, TokenCacheConfig tokenCache) {
        this.microOauth = microOauth;
        this.jettyConfig = jettyConfig;
        this.clientDatabase = clientDatabase;
        this.tokenCache = tokenCache;
    }

    public static MicroOauthConfiguration generateDefaultConfig() {
        return new MicroOauthConfiguration(OauthConfig.generateDefaultConfig(), JettyConfig.generateDefaultConfig(),
                ClientDatabaseConfig.generateDefaultConfig(), TokenCacheConfig.generateDefaultConfig());
    }

    public OauthConfig getMicroOauth() { return microOauth; }

    public void setMicroOauth(OauthConfig microOauth) { this.microOauth = microOauth; }

    public JettyConfig getJettyConfig() { return jettyConfig; }

    public void setJettyConfig(JettyConfig jettyConfig) { this.jettyConfig = jettyConfig; }

    public ClientDatabaseConfig getClientDatabase() { return clientDatabase; }

    public void setClientDatabase(ClientDatabaseConfig clientDatabase) { this.clientDatabase = clientDatabase; }

    public TokenCacheConfig getTokenCache() { return tokenCache; }

    public void setTokenCache(TokenCacheConfig tokenCache) { this.tokenCache = tokenCache; }
}
