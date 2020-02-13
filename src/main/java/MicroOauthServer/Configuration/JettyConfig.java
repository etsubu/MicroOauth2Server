package MicroOauthServer.Configuration;

/**
 * Configurations for the jetty server
 */
public class JettyConfig {
    private int port;
    private String keyStorePath;

    /**
     * Initializes JettyConfig
     * @param port Port to bind jetty on
     * @param keyStorePath Keystore for https if enabled
     */
    public JettyConfig(int port, String keyStorePath) {
        this.port = port;
        this.keyStorePath = keyStorePath;
    }

    /**
     * Empty constructor for snakeyaml
     */
    public JettyConfig() {

    }

    public int getPort() { return port; }

    public String getKeyStorePath() { return keyStorePath; }

    public static JettyConfig generateDefaultConfig() {
        return new JettyConfig(80, "jetty.jks");
    }
}
