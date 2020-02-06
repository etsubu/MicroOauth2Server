package MicroOauthServer.Token;

import com.google.gson.annotations.SerializedName;

public class AuthorizationToken {
    public static final String BEARER_TYPE = "Bearer";
    @SerializedName("access_token")
    private final String access_token;
    @SerializedName("token_type")
    private final String token_type;
    @SerializedName("expires_in")
    private final long expires_in;
    @SerializedName("refresh_token")
    private final String refresh_token;
    @SerializedName("scope")
    private final String scope;

    public AuthorizationToken(String access_token, String token_type, long expiresIn) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expiresIn;
        this.refresh_token = null;
        this.scope = null;
    }

    public AuthorizationToken(String access_token, String token_type, long expiresIn, String refresh_token, String scope) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expiresIn;
        this.refresh_token = refresh_token;
        this.scope = scope;
    }

    public String getAccess_token() { return access_token; }

    public String getToken_type() { return token_type; }

    public long getExpires_in() { return expires_in; }

    public String getRefresh_token() { return  refresh_token; }

    public String getScope() { return scope; }
}
