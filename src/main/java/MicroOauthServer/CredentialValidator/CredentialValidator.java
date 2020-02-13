package MicroOauthServer.CredentialValidator;

import java.util.Map;

/**
 * CredentialValidator interface defines the API for authenticating user with their credentials
 * @author etsubu
 */
public interface CredentialValidator {

    /**
     * Authenticate user's credentials
     * @param username Username
     * @param password Password
     * @param optional Optional KEY:VALUE map that will be attached to the JSON request
     * @return True user credentials were valid, false not
     */
    boolean authenticate(String username, String password, Map<String, Object> optional);
}
