package MicroOauthServer.CredentialValidator;

import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * SimpleCredentialValidator used for testing
 * @author etsubu
 */
public class SimpleCredentialValidator implements CredentialValidator {

    @Override
    public boolean authenticate(String username, String password, Map<String, Object> optional) {
        return username.equals("test_user") && password.equals("test_password");
    }
}
