package MicroOauthServer.ClientDatabase;

import MicroOauthServer.Clients.OauthClient;
import MicroOauthServer.Configuration.Configuration;
import MicroOauthServer.Sdk.Annotations.ClientStorageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ClientStorageController(name = "SQL")
public class SqlClientStorageController implements ClientStorageAPI {
    private static final Logger log = LoggerFactory.getLogger(SqlClientStorageController.class);
    private static final String OAUTH_CLIENT_TABLE = "OauthClients";
    private static final String REDIRECT_TABLE     = "ClientRedirects";
    private Connection connection;
    private Configuration config;

    public SqlClientStorageController(Configuration config) {
        this.config = config;
        init();
    }

    private void init() {
        try {
            String url = config.getClientDatabase().getHostname();
            // create a connection to the database
            log.info("Initiating database connection: " + url);
            connection = DriverManager.getConnection(url);
            log.info("SQL database connection established");

            createClientTables();

            log.info("SQL tables initialized!");
        } catch (SQLException e) {
            log.error("Failed to open database connection ", e);
        }
    }

    public void createClientTables() {
        String createOauthClientTable  = "CREATE TABLE IF NOT EXISTS " + OAUTH_CLIENT_TABLE     + " (id int,clientId varchar(64),scopes varchar(255),secret varchar(64),description varchar(255));";
        String createRedirectsUris     = "CREATE TABLE IF NOT EXISTS " + REDIRECT_TABLE         + " (id int,clientId varchar(64),redirectUri varchar(255));";

        try {
            Statement stmt = connection.createStatement();
            stmt.addBatch(createOauthClientTable);
            stmt.addBatch(createRedirectsUris);
            // Create all the tables
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean executeForRedirectTable(String statement, String clientId, Set<String> redirectUris) {
        try {
            PreparedStatement stmt = connection.prepareStatement(statement);
            redirectUris.forEach(x -> {
                try {
                    stmt.setString(1, clientId);
                    stmt.setString(2, x);
                    stmt.addBatch();
                } catch (SQLException e) {
                    log.error("Failed to build SQL statement ", e);
                }
            });
            // Create all the tables
            return Arrays.stream(stmt.executeBatch()).anyMatch(x -> x > 0);
        } catch (SQLException e) {
            log.error("SQL statement failed ", e);
        }
        return false;
    }

    @Override
    public boolean addClient(OauthClient client) {
        String createOauthClientTable  = "INSERT INTO " + OAUTH_CLIENT_TABLE + " (clientId, scopes, secret, description) " +
                                         "VALUES (?, ?, ?, ?); ";
        String createRedirectUris  = "INSERT INTO " + REDIRECT_TABLE + " (clientId, redirectUri) " +
                "VALUES (?, ?);";
        try {
            PreparedStatement statement = connection.prepareStatement(createOauthClientTable);
            statement.setString(1, client.getClientId());
            statement.setString(2, String.join(" ", client.getScopes()));
            statement.setString(3, client.getSecret());
            statement.setString(4, client.getDescription());
            log.info("Storing new oauth client");
            if(statement.executeUpdate() != 0) {
                return executeForRedirectTable(createRedirectUris, client.getClientId(), client.getRedirectUris());
            }
        } catch (SQLException e) {
            log.error("OauthClient database statement failed ", e);
        }
        return false;
    }

    @Override
    public Optional<OauthClient> queryClient(String clientId) {
        String queryClientStatement  = "SELECT * FROM " + OAUTH_CLIENT_TABLE + " WHERE clientId=?;";
        String queryClientRedirects  = "SELECT redirectUri FROM " + REDIRECT_TABLE + " WHERE clientId=?;";
        try {
            PreparedStatement statement = connection.prepareStatement(queryClientStatement);
            statement.setString(1, clientId);
            log.info("Querying oauth client");
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                Set<String> redirectUris = new HashSet<>();
                PreparedStatement redirectUriStatement = connection.prepareStatement(queryClientRedirects);
                redirectUriStatement.setString(1, clientId);
                ResultSet redirectResults = redirectUriStatement.executeQuery();
                while(redirectResults.next()) {
                    String redirectUri = redirectResults.getString("redirectUri");
                    if(redirectUri != null) {
                        redirectUris.add(redirectUri);
                    }
                }
                String scopes = result.getString("scopes");
                String secret = result.getString("secret");
                String description = result.getString("description");
                return Optional.of(new OauthClient(clientId, Set.of(scopes.split(" ")), redirectUris, secret, description));
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error("OauthClient database statement failed ", e);
            return Optional.empty();
        }
    }

    @Override
    public boolean removeClient(String clientId) {
        String deleteClientStatement  = "DELETE FROM " + OAUTH_CLIENT_TABLE + " WHERE clientId=?;";
        String deleteRedirectUris     = "DELETE FROM " + REDIRECT_TABLE     + " WHERE clientId=?;";
        try {
            PreparedStatement statement = connection.prepareStatement(deleteClientStatement);
            statement.setString(1, clientId);
            PreparedStatement redirectUriStatements = connection.prepareStatement(deleteRedirectUris);
            redirectUriStatements.setString(1, clientId);
            log.info("Deleting client id");
            boolean clientRemoval = statement.executeUpdate() != 0;
            return clientRemoval && redirectUriStatements.executeUpdate() != 0 ;
        } catch (SQLException e) {
            log.error("OauthClient database statement failed ", e);
            return false;
        }
    }
}
