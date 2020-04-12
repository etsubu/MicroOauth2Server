package IntegrationTests.GrantTypes;

import MicroOauthServer.ClientDatabase.ClientAuthenticationException;
import MicroOauthServer.ClientDatabase.ClientManager;
import MicroOauthServer.Configuration.ConfigurationService;
import MicroOauthServer.Configuration.MicroOauthConfiguration;
import MicroOauthServer.Crypto.SymmetricCrypto;
import MicroOauthServer.JettyStarter;
import MicroOauthServer.Sdk.Annotations.Scopes.Scopes;
import MicroOauthServer.Token.AuthorizationToken;
import com.google.gson.Gson;
import io.netty.handler.codec.base64.Base64Decoder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Integration tests for client credentials grant type
 * @author etsubu
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class, JettyStarter.class}, webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientCredentialsTest {
    private HttpClient httpClient;

    @LocalServerPort
    private int port;

    @Autowired
    private ClientManager clientManager;

    @Autowired
    private ConfigurationService configuration;

    private static final String CLIENT_ID = "test-client";
    private static final String CLIENT_SECRET = "test-secret";

    @Before
    public void init() throws ClientAuthenticationException {
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        // Create test client
        clientManager.createClient(CLIENT_ID, Set.of("account-mgmt", Scopes.TOKEN_INTROSPECT, "test-scope"),
                Set.of("http://localhost/healthcheck"), CLIENT_SECRET, "Test client");
    }

    public static HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    private String sendPOST(String url, String clientId, String secret) throws IOException, InterruptedException {

        // form parameters
        Map<Object, Object> data = new HashMap<>();
        data.put("grant_type", "client_credentials");
        data.put("client_id", clientId);
        data.put("client_secret", secret);
        data.put("audience", "YOUR_API_IDENTIFIER");

        HttpRequest request = HttpRequest.newBuilder()
                .POST(ofFormData(data))
                .uri(URI.create(url))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private HttpResponse<String> introspectToken(String token, String accessToken) throws IOException, InterruptedException {
        // form parameters
        Map<Object, Object> data = new HashMap<>();
        data.put("token", token);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(ofFormData(data))
                .uri(URI.create("http://localhost/oauth/introspect"))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .setHeader("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Use client credentials grant type to obtain a valid access token
     * @throws IOException Connection error
     * @throws InterruptedException Connection error
     */
    @Test
    public void testValidClientCredentialsObtainToken() throws IOException, InterruptedException {
        String response = sendPOST("http://localhost:" + port + "/oauth/token", CLIENT_ID, CLIENT_SECRET);
        Gson gson = new Gson();
        AuthorizationToken token = gson.fromJson(response, AuthorizationToken.class);
        // Valid expire time
        assertTrue(token.getExpires_in() > 0);
        // Verify correct token length
        assertEquals(configuration.getMicroOauth().getAccessTokenLength() * 2 +
                SymmetricCrypto.GCM_TAG_LENGTH +
                SymmetricCrypto.GCM_IV_LENGTH, Base64.getDecoder().decode(token.getAccess_token()).length);
    }

    /**
     * Tries to obtain access token with invalid client credentials
     * @throws IOException Connection error
     * @throws InterruptedException Connection error
     */
    @Test
    public void testInvalidClientCredentials() throws IOException, InterruptedException {
        String response = sendPOST("http://localhost:" + port + "/oauth/token", CLIENT_ID, "invalid_secret");
        System.out.println(response);
        Gson gson = new Gson();
    }
}
