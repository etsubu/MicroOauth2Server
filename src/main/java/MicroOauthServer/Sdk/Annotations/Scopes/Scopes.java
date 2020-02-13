package MicroOauthServer.Sdk.Annotations.Scopes;

/**
 * List of built in scopes which are used by MicroOauth server internals
 */
public class  Scopes {

    /**
     * Required to access the oauth token introspect endpoint
     */
    public static final String TOKEN_INTROSPECT = "token-introspect";

    public static String buildScopes(String... scopes) {
        if(scopes == null)
            return null;
        return String.join(" ", scopes);
    }
}
