package Token;

public class BearerToken extends AuthorizationToken{
    public BearerToken(String token, int expiresIn) {
        super(token, "Bearer", expiresIn);
    }
}
