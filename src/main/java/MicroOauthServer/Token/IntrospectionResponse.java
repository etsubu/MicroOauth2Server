package MicroOauthServer.Token;

import com.google.gson.annotations.SerializedName;

public class IntrospectionResponse {
    public static final IntrospectionResponse EXPIRED = new IntrospectionResponse(false);
    private boolean active;

    private String scope;

    @SerializedName("client_id")
    private String clientId;

    @SerializedName("username")
    private String username;

    @SerializedName("token_type")
    private String type;

    @SerializedName("exp")
    private Long expirationTime;

    @SerializedName("iat")
    private Long generationTime;

    @SerializedName("nbf")
    private Long notValidBeforeTime;

    @SerializedName("sub")
    private String tokenAuthorizer;

    @SerializedName("aud")
    private String audience;

    @SerializedName("iss")
    private String tokenIssuer;

    @SerializedName("jti")
    private String tokenIdentifier;

    /**
     * InstrospectionResponse Builder
     */
    public static class Builder {
        private IntrospectionResponse introspectionResponse;

        public Builder() {
            introspectionResponse = new IntrospectionResponse();
        }

        public Builder setActive(boolean active) {
            introspectionResponse.setActive(active);
            return this;
        }

        public Builder setScope(String scope) {
            introspectionResponse.setScope(scope);
            return this;
        }

        public Builder setClientId(String clientId) {
            introspectionResponse.setClientId(clientId);
            return this;
        }

        public Builder setType(String type) {
            introspectionResponse.setType(type);
            return this;
        }

        public IntrospectionResponse build() {
            return introspectionResponse;
        }
    }

    public IntrospectionResponse() {
        this.active = false;
    }

    public IntrospectionResponse(boolean active) {
        this.active = false;
    }

    public void setActive(boolean active) {this.active = active; }

    public void setClientId(String clientId) { this.clientId = clientId; }

    public void setScope(String scope) { this.scope = scope; }

    public void setType(String type) { this.type = type; }
}
