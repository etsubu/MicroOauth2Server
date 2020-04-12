package MicroOauthServer.Authentication;

import MicroOauthServer.Sdk.Annotations.AuthenticationController;

import java.util.Arrays;
import java.util.Map;

@AuthenticationController(name = "SimpleAuthenticator")
public class SimpleAuthenticator extends MicroOauthServer.Authentication.AuthenticationController {
    @Override
    public AuthenticationResponse authenticate(String username, char[] password, Map<String, String> metadata) {
        return new AuthenticationResponse("test-user".equals(username) &&
                Arrays.equals("test-password".toCharArray(), password), null, null);
    }

    @Override
    public void authenticateOTP(String username, String code) {

    }
}
