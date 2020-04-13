package IntegrationTests.GrantTypes;

import MicroOauthServer.ClientDatabase.ClientAuthenticationException;
import MicroOauthServer.ClientDatabase.ClientManager;
import MicroOauthServer.Configuration.ConfigurationService;
import MicroOauthServer.Configuration.MicroOauthConfiguration;
import MicroOauthServer.Crypto.SymmetricCrypto;
import MicroOauthServer.Exceptions.InvalidClientException;
import MicroOauthServer.Exceptions.InvalidRequestException;
import MicroOauthServer.Exceptions.OauthException;
import MicroOauthServer.JettyStarter;
import MicroOauthServer.Sdk.Annotations.Scopes.Scopes;
import MicroOauthServer.Token.AuthorizationToken;
import com.google.gson.Gson;
import io.netty.handler.codec.base64.Base64Decoder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static junit.framework.TestCase.*;

/**
 * Integration tests for client credentials grant type
 * @author etsubu
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class, JettyStarter.class}, webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientCredentialsTest {
    private static final Logger log = LoggerFactory.getLogger(ClientCredentialsTest.class);
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
            if(entry.getValue() != null) {
                if (builder.length() > 0) {
                    builder.append("&");
                }
                builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
            }
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    private String generateRandomState() {
        Random rand = new Random();
        int length = 3 + rand.nextInt(8);
        StringBuilder builder = new StringBuilder(length);
        for(int i = 0; i < length; i++) {
            builder.append('a' + (char)rand.nextInt('z' - 'a'));
        }
        return builder.toString();
    }

    private HttpResponse<String> sendPOST(String url, String clientId, String secret) throws IOException, InterruptedException {

        // form parameters
        Map<Object, Object> data = new HashMap<>();
        data.put("grant_type", "client_credentials");
        data.put("client_id", clientId);
        data.put("client_secret", secret);
        data.put("audience", "YOUR_API_IDENTIFIER");

        return sendPOST(url, data);
    }

    private HttpResponse<String> sendPOST(String url, Map<Object, Object> data) throws IOException, InterruptedException {
        log.info("HTTP POST to {}", url);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(ofFormData(data))
                .uri(URI.create(url))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
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
        HttpResponse<String> response = sendPOST("http://localhost:" + port + "/oauth/token", CLIENT_ID, CLIENT_SECRET);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        Gson gson = new Gson();
        AuthorizationToken token = gson.fromJson(response.body(), AuthorizationToken.class);
        // Valid expire time
        assertTrue(token.getExpires_in() > 0);
        // Verify correct token length
        assertEquals(configuration.getMicroOauth().getAccessTokenLength() * 2 +
                SymmetricCrypto.GCM_TAG_LENGTH +
                SymmetricCrypto.GCM_IV_LENGTH, Base64.getDecoder().decode(token.getAccess_token()).length);
        // Refresh token must be null for client credentials authorization
        assertNull(token.getRefresh_token());
    }

    /**
     * Tries to obtain access token with invalid client credentials
     * @throws IOException Connection error
     * @throws InterruptedException Connection error
     */
    @Test
    public void testInvalidClientCredentials() throws IOException, InterruptedException {
        HttpResponse<String> response = sendPOST("http://localhost:" + port + "/oauth/token", CLIENT_ID, "invalid_secret");
        Gson gson = new Gson();
        // We should receive unauthorized status code
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
        OauthException exception = gson.fromJson(response.body(), OauthException.class);
        // Verify error contents
        assertEquals(InvalidClientException.INVALID_CLIENT, exception.getError());
        assertEquals(InvalidClientException.INVALID_CLIENT_OR_SECRET, exception.getDescription());

        // Try to authenticate with incorrect client id
        response = sendPOST("http://localhost:" + port + "/oauth/token", "invalid_client", CLIENT_SECRET);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.statusCode());
        exception = gson.fromJson(response.body(), OauthException.class);
        // Verify error contents
        assertEquals(InvalidClientException.INVALID_CLIENT, exception.getError());
        assertEquals(InvalidClientException.INVALID_CLIENT_OR_SECRET, exception.getDescription());
    }

    @Test
    public void testMissingClientCredentials() throws IOException, InterruptedException {
        HttpResponse<String> response = sendPOST("http://localhost:" + port + "/oauth/token", CLIENT_ID, null);
        Gson gson = new Gson();
        // We should receive unauthorized status code
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        OauthException exception = gson.fromJson(response.body(), OauthException.class);
        // Verify that client secret was missing
        assertEquals(InvalidClientException.INVALID_CLIENT, exception.getError());
        assertEquals(InvalidClientException.CLIENT_SECRET_MISSING, exception.getDescription());

        // Try to authenticate with client id missing
        response = sendPOST("http://localhost:" + port + "/oauth/token", null, CLIENT_SECRET);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        exception = gson.fromJson(response.body(), OauthException.class);
        // Verify that client id was missing
        assertEquals(InvalidClientException.INVALID_CLIENT, exception.getError());
        assertEquals(InvalidClientException.CLIENT_ID_MISSING, exception.getDescription());
    }

    private void validateBadRequest(OauthException exception, int statusCode) {
        assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);
        assertEquals(InvalidRequestException.INVALID_REQUEST, exception.getError());
        assertNotNull(exception.getDescription());
    }

    @Test
    public void testAuthorizeEndpointMissingParameters() throws IOException, InterruptedException {
        String state = generateRandomState();
        log.info("Generated state parameter {}", state);
        String[] urls = new String[] {"client_id=" + CLIENT_ID,
                "response_type=token",
                "state=" + state,
                "redirect_uri=" + URLEncoder.encode("http://localhost/oauth/healthcheck", StandardCharsets.UTF_8)};

        Map<Object, Object> data = new HashMap<>();
        data.put("username", "test-user");
        data.put("password", "test-password");
        Gson gson = new Gson();
        StringBuilder builder = new StringBuilder("http://localhost:" + port + "/authorize");

        HttpResponse<String> response = sendPOST(builder.toString(), new HashMap<>());
        validateBadRequest(gson.fromJson(response.body(), OauthException.class), response.statusCode());

        // Validate that request fails if one of the parameters is missing
        for(int i = 0; i < urls.length; i++) {
            builder = new StringBuilder("http://localhost:" + port + "/authorize");
            boolean first = true;
            for(int j = 0; j < urls.length; j++) {
                if(i == j) {
                    continue;
                }
                builder.append(first ? '?' : '&')
                        .append(urls[j]);
                first = false;
            }
            // Validate request
            response = sendPOST(builder.toString(), data);
            validateBadRequest(gson.fromJson(response.body(), OauthException.class), response.statusCode());
        }

        // Validate that we don't actually get any tokens if credentials are missing
        response = sendPOST(builder.toString(), new HashMap<>());
        validateBadRequest(gson.fromJson(response.body(), OauthException.class), response.statusCode());

        builder.append('&')
                .append(urls[urls.length - 1]);
        // Send a valid request
        response = sendPOST(builder.toString(), data);
        // Validate that we received redirect
        assertEquals(HttpStatus.FOUND.value(), response.statusCode());
        // Validate that the redirect is the one we requested
        String redirect = response.headers().firstValue("Location").get();
        log.info("Received redirect: {}", redirect);
        // Verify that the redirect contains access token and other parameters, as well as correct state parameter
        assertTrue(redirect.contains("access_token="));
        assertTrue(redirect.contains("state=" + state));
        assertTrue(redirect.contains("expires_in="));
        assertTrue(redirect.contains("?"));
        // Verify that the redirect path is correct
        assertEquals("http://localhost/oauth/healthcheck", redirect.substring(0, redirect.indexOf('?')));

    }
}
