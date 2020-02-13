package MicroOauthServer.Configuration;

/**
 * Configurations for the used token cache
 * @author etsubu
 */
public class TokenCacheConfig {
    /**
     * Name of the database controller, e.g. redis, mysql,
     */
    private String controllerName;

    /**
     * Hostname of the server where the token cache is hosted
     */
    private String hostname;

    /**
     * Port of the token cache server
     */
    private int port;

    /**
     * Initializes TokenCacheConfig
     * @param controllerName Token cache controller name
     * @param hostname Hostname of the token cache server
     * @param port Port number of the token cache server
     */
    public TokenCacheConfig(String controllerName, String hostname, int port) {
        this.controllerName = controllerName;
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Empty constructor for snakeyaml
     */
    public TokenCacheConfig() {

    }

    public static TokenCacheConfig generateDefaultConfig() {
        return new TokenCacheConfig("SQL", "localhost", 3306);
    }

    public String getControllerName() { return controllerName; }

    public void setControllerName(String controllerName) { this.controllerName = controllerName; }

    public String getHostname() { return hostname;}

    public void setHostname(String hostname) { this.hostname = hostname; }

    public int getPort() { return port; }

    public void setPort(int port) { this.port = port; }
}
