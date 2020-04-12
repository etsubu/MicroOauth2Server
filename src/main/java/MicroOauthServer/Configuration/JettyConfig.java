package MicroOauthServer.Configuration;

/**
 * Configurations for the jetty server
 * @author etsubu
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

    /**
     *
     * @return Port which jetty should bind on
     */
    public int getPort() { return port; }

    public void setPort(int port) { this.port = port;}

    /**
     *
     * @return Keystore path to be used for HTTPS
     */
    public String getKeyStorePath() { return keyStorePath; }

    /**
     *
     * @return Default configuration
     */
    public static JettyConfig generateDefaultConfig() {
        return new JettyConfig(80, "jetty.jks");
    }
}
