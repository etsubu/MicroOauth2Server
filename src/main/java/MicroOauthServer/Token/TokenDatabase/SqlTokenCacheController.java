package MicroOauthServer.Token.TokenDatabase;

import MicroOauthServer.ClientDatabase.SqlClientStorageController;
import MicroOauthServer.Configuration.ConfigurationService;
import MicroOauthServer.Configuration.MicroOauthConfiguration;
import MicroOauthServer.Sdk.Annotations.TokenCacheController;
import MicroOauthServer.Token.StorageToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

@TokenCacheController(name = "SQL")
public class SqlTokenCacheController implements TokenStorageAPI {
    private static final Logger log = LoggerFactory.getLogger(SqlClientStorageController.class);
    private static final String TOKENS_TABLE = "Tokens";
    private Connection connection;
    private ConfigurationService config;

    public SqlTokenCacheController(ConfigurationService config) {
        this.config = config;
        init();
    }

    private void init() {
        try {
            String url = config.getTokenCache().getHostname();
            // create a connection to the database
            log.info("Initiating token cache database connection: " + url);
            connection = DriverManager.getConnection(url);
            log.info("SQL token cache connection established");

            if(createTokensTable()) {
                log.info("SQL token cache initialized!");
            }
        } catch (SQLException e) {
            log.error("Failed to open database connection ", e);
        }
    }

    private boolean createTokensTable() {
        // We use two separate 32 bit int fields for long as we want to keep the implementation general purpose so it works with all databases
        String createTokensTable  = "CREATE TABLE IF NOT EXISTS " + TOKENS_TABLE     + " (clientId varchar(64),token varchar(48),scopes varchar(255),"
                                    +"token_type varchar(32),redirectUri varchar(128), generatedTimestampLow INT, generatedTimeStampHigh, TTLLow INT, TTLHigh INT);";

        try {
            Statement stmt = connection.createStatement();
            return stmt.execute(createTokensTable);
        } catch (SQLException e) {
            log.error("Failed to create tokens table");
        }
        return false;
    }

    @Override
    public Optional<StorageToken> queryToken(String key) {
        String createOauthClientTable  = "SELECT * FROM " + TOKENS_TABLE + " WHERE token=?;";
        try {
            PreparedStatement statement = connection.prepareStatement(createOauthClientTable);
            statement.setString(1, key);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                String clientId = result.getString("clientId");
                String redirectUri = result.getString("redirectUri");
                long generatedTimestamp = ((long)result.getInt("generatedTimestampLow") & 0xFFFFFFFFL) | ((long)result.getInt("generatedTimestampHigh") << 32);
                long TTL = ((long)result.getInt("TTLLow") & 0xFFFFFFFFL) | ((long)result.getInt("TTLHigh") << 32);
                String type = result.getString("token_type");
                String scopes = result.getString("scopes");
                return Optional.of(new StorageToken(clientId, key, type, scopes, generatedTimestamp, TTL, redirectUri));
            }
        } catch (SQLException e) {
            log.error("SQL query token statement failed ", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<StorageToken> popToken(String key) {
        Optional<StorageToken> token = queryToken(key);
        token.ifPresent(this::revokeToken);
        return token;
    }

    @Override
    public boolean addToken(StorageToken token) {
        String addTokenStatement  = "INSERT INTO " + TOKENS_TABLE + " (clientId, token, scopes, token_type, redirectUri, " +
                "generatedTimestampLow, generatedTimeStampHigh, TTLLow, TTLHigh) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?); ";
        try {
            PreparedStatement statement = connection.prepareStatement(addTokenStatement);
            statement.setString(1, token.getClientId());
            statement.setString(2, token.getToken());
            statement.setString(3, token.getScopes());
            statement.setString(4, token.getType());
            statement.setString(5, token.getRedirectUri());
            statement.setInt(6, (int) (token.getGeneratedTimestamp() & 0xFFFFFFFFL));
            statement.setInt(7, (int) (token.getGeneratedTimestamp() >> 32));
            statement.setInt(8, (int) (token.getTTL() & 0xFFFFFFFFL));
            statement.setInt(9, (int) (token.getTTL() >> 32));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("SQL add token statement failed ", e);
        }
        return false;
    }

    @Override
    public boolean revokeToken(StorageToken token) {
        String deleteTokenStatement  = "DELETE FROM " + TOKENS_TABLE + " WHERE token=?;";
        try {
            PreparedStatement statement = connection.prepareStatement(deleteTokenStatement);
            statement.setString(1, token.getToken());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("SQL revoke token statement failed ", e);
        }
        return false;
    }

    @Override
    public boolean revokeAllTokensForClient(String clientId) {
        String deleteTokenStatement  = "DELETE FROM " + TOKENS_TABLE + " WHERE clientId=?;";
        try {
            PreparedStatement statement = connection.prepareStatement(deleteTokenStatement);
            statement.setString(1, clientId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("SQL revoke all tokens for client failed ", e);
        }
        return false;
    }
}
