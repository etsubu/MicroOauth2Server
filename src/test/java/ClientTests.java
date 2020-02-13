import MicroOauthServer.Token.AuthorizationToken;
import com.google.gson.Gson;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class ClientTests {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

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

    private String sendPOST(String url) throws IOException, InterruptedException {

        // form parameters
        Map<Object, Object> data = new HashMap<>();
        data.put("grant_type", "client_credentials");
        data.put("client_id", "test-client");
        data.put("client_secret", "test-secret");
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

    @Test
    public void postClientFlow() {

        try {
            String response = sendPOST("http://localhost/oauth/token");
            Gson gson = new Gson();
            System.out.println(response);
            AuthorizationToken token = gson.fromJson(response, AuthorizationToken.class);
            System.out.println(token.getAccess_token());
            HttpResponse<String> tokenIntrospect = introspectToken(token.getAccess_token(), token.getAccess_token());
            System.out.println(tokenIntrospect.body());
            assertEquals(200, tokenIntrospect.statusCode());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
