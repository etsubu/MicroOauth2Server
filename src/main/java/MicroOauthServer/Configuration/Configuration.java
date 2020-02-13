package MicroOauthServer.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.yaml.snakeyaml.constructor.Constructor;

import javax.annotation.PostConstruct;

/**
 * Base configuration class for the oauth server
 * @author etsubu
 */
@Component
public class Configuration {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);
    private MicroOauthConfig microOauth;
    private JettyConfig jettyConfig;
    private ClientDatabaseConfig clientDatabase;
    private TokenCacheConfig tokenCache;

    /**
     * Empty constructor for snakeyaml
     */
    public Configuration() {

    }

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Configuration config = mapper.readValue(new File("config.yaml"), Configuration.class);
            mapper.findAndRegisterModules();
            this.microOauth     = config.getMicroOauth();
            this.jettyConfig    = config.getJettyConfig();
            this.clientDatabase = config.getClientDatabase();
            this.tokenCache     = config.getTokenCache();
            return;
        } catch (IOException e) {
            if(Files.exists(Paths.get("config.yaml"))) {
                log.error("Configuration file could not be deserialized ", e);
                System.exit(0);
                throw new RuntimeException("Invalid configuration");
            }
        }
        Configuration config = Configuration.generateDefaultConfig();
        this.microOauth     = config.getMicroOauth();
        this.jettyConfig    = config.getJettyConfig();
        this.clientDatabase = config.getClientDatabase();
        this.tokenCache     = config.getTokenCache();

        try{
            mapper.writeValue(new File("config.yaml"), config);
        } catch (IOException e) {
            log.error("Failed to save default configuration to a file ", e);
        }
    }

    public Configuration(Configuration c) {
        this.microOauth = c.getMicroOauth();
        this.jettyConfig = c.getJettyConfig();
        this.clientDatabase = c.getClientDatabase();
        this.tokenCache = c.getTokenCache();
    }

    public Configuration(MicroOauthConfig microOauth, JettyConfig jettyConfig, ClientDatabaseConfig clientDatabase, TokenCacheConfig tokenCache) {
        this.microOauth = microOauth;
        this.jettyConfig = jettyConfig;
        this.clientDatabase = clientDatabase;
        this.tokenCache = tokenCache;
    }

    public static Configuration generateDefaultConfig() {
        return new Configuration(MicroOauthConfig.generateDefaultConfig(), JettyConfig.generateDefaultConfig(),
                ClientDatabaseConfig.generateDefaultConfig(), TokenCacheConfig.generateDefaultConfig());
    }

    public MicroOauthConfig getMicroOauth() { return microOauth; }

    public void setMicroOauth(MicroOauthConfig microOauth) { this.microOauth = microOauth; }

    public JettyConfig getJettyConfig() { return jettyConfig; }

    public void setJettyConfig(JettyConfig jettyConfig) { this.jettyConfig = jettyConfig; }

    public ClientDatabaseConfig getClientDatabase() { return clientDatabase; }

    public void setClientDatabase(ClientDatabaseConfig clientDatabase) { this.clientDatabase = clientDatabase; }

    public TokenCacheConfig getTokenCache() { return tokenCache; }

    public void setTokenCache(TokenCacheConfig tokenCache) { this.tokenCache = tokenCache; }
}
