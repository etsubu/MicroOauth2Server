package MicroOauthServer.Configuration;

public class ClientDatabaseConfig {
    /**
     * Name of the database controller, e.g. redis, mysql,
     */
    private String controllerName;

    /**
     * Hostname of the server where the oauth clients are stored
     */
    private String hostname;

    /**
     * Port of the client database server
     */
    private int port;

    /**
     * Initializes ClientDatabaseConfig
     * @param controllerName Client database controller name
     * @param hostname Hostname of the Client database server
     * @param port Port number of the Client database server
     */
    public ClientDatabaseConfig(String controllerName, String hostname, int port) {
        this.controllerName = controllerName;
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Empty constructor for snakeyaml
     */
    public ClientDatabaseConfig() {

    }

    public static ClientDatabaseConfig generateDefaultConfig() {
        return new ClientDatabaseConfig("SQL", "clientStorage.db", 3306);
    }

    public String getControllerName() { return controllerName; }

    public void setControllerName(String controllerName) { this.controllerName = controllerName; }

    public String getHostname() { return hostname;}

    public void setHostName(String hostname) { this.hostname = hostname; }

    public int getPort() { return port; }

    public void setPort(int port) { this.port = port; }
}
