package Token;

public class AuthorizationToken {
    private String token;
    private String type;
    private int expiresIn;

    public AuthorizationToken(String token, String type, int expiresIn) {
        this.token = token;
        this.type = type;
        this.expiresIn = expiresIn;
    }
}
