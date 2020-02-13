package MicroOauthServer.Configuration;

/**
 * MicroOauth general configuration
 * @author etsubu
 */
public class MicroOauthConfig {
    private String keystorePath;
    private char[] keystorePassword;
    private long accessTokenTTL;
    private long refreshTokenTTL;
    private long authorizationCodeTTL;
    private int authorizationCodeLength;
    private int accessTokenLength;
    private int refreshTokenLength;

    /**
     * Empty constructor for snakeyaml
     */
    public MicroOauthConfig() {

    }

    public MicroOauthConfig(String keystorePath, char[] keystorePassword, long accessTokenTTL, long refreshTokenTTL, long authorizationCodeTTL,
                            int authorizationCodeLength, int accessTokenLength, int refreshTokenLength) {
        this.keystorePath = keystorePath;
        this.keystorePassword = keystorePassword;
        this.accessTokenTTL = accessTokenTTL;
        this.refreshTokenTTL = refreshTokenTTL;
        this.authorizationCodeTTL = authorizationCodeTTL;
        this.authorizationCodeLength = authorizationCodeLength;
        this.accessTokenLength = accessTokenLength;
        this.refreshTokenLength = refreshTokenLength;
    }

    public static MicroOauthConfig generateDefaultConfig() {
        return new MicroOauthConfig("keys.ks", "defaultKey".toCharArray(), 60 * 5, 60 * 60 * 24 * 30, 60 * 2, 12, 16, 32);
    }

    public String getKeystorePath() { return keystorePath; }

    public void setKeystorePath(String keystorePath) { this.keystorePath = keystorePath; }

    public char[] getKeystorePassword() { return keystorePassword; }

    public void setKeystorePassword(char[] keystorePassword) { this.keystorePassword = keystorePassword; }

    public long getAccessTokenTTL() { return accessTokenTTL; }

    public void setAccessTokenTTL(long accessTokenTTL) { this.accessTokenTTL = accessTokenTTL; }

    public long getRefreshTokenTTL() { return refreshTokenTTL; }

    public void setRefreshTokenTTL(long refreshTokenTTL) { this.refreshTokenTTL = refreshTokenTTL; }

    public long getAuthorizationCodeTTL() { return authorizationCodeTTL; }

    public void setAuthorizationCodeTTL(long authorizationCodeTTL) { this.authorizationCodeTTL = authorizationCodeTTL; }

    public int getAuthorizationCodeLength() { return authorizationCodeLength; }

    public void setAuthorizationCodeLength(int authorizationCodeLength) { this.authorizationCodeLength = authorizationCodeLength; }

    public int getAccessTokenLength() { return accessTokenLength; }

    public void setAccessTokenLength(int accessTokenLength) { this.accessTokenLength = accessTokenLength; }

    public int getRefreshTokenLength() { return refreshTokenLength; }

    public void setRefreshTokenLength(int refreshTokenLength) { this.refreshTokenLength = refreshTokenLength;}
}
