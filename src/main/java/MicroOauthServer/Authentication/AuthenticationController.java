package MicroOauthServer.Authentication;

import java.util.Map;

/**
 * Authentication Controller defines the interface for authenticating users. User storages are usually service specific
 * so it is not reasonable to expect one controller to work for every service. In some circumstances the database access
 * and authentication can be implemented in MicroOauth server or then it can be integrated to perform remote authentication
 * through REST API. All authentication controller implementations must implement this interface
 * @author etsubu
 */
public abstract class AuthenticationController {

    /**
     * Authentication with user's credentials
     * @param username User authenticating
     * @param password User's password
     * @param metadata Optional. Potential additional metadata fields to pass with authentication request (ip address,
     *                 user agent, etc.)
     * @return
     */
    public abstract AuthenticationResponse authenticate(String username, char[] password, Map<String, String> metadata);

    /**
     * Authentication with user's credentials
     * @param username User authenticating
     * @param password User's password
     * @return
     */
    public AuthenticationResponse authenticate(String username, char[] password) {
        return authenticate(username, password, null);
    }

    /**
     * Authentication for 2FA OTP
     * @param username User authenticating
     * @param code One Time Password code
     */
    public abstract void authenticateOTP(String username, String code);
}
