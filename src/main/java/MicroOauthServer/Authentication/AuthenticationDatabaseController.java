package MicroOauthServer.Authentication;

import MicroOauthServer.Sdk.Annotations.AuthenticationController;

/**
 * Authentication Controller which queries user credentials from SQL database and validates those. Note that for this
 * controller to work the field names in database need to match those used by this controller and the password hash
 * must be in supported format and use a supported hashing algorithm
 * @author etsubu
 */
@AuthenticationController(name = "SQLAuthenticationDatabaseController")
public class AuthenticationDatabaseController {
    String createRedirectsUris     = "SELECT username,password FROM users WHERE username=";
}
