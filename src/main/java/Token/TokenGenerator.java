package Token;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {
    public static final int ACCESS_TOKEN_LENGTH = 32;
    private SecureRandom rand;

    public TokenGenerator() {
        rand = new SecureRandom();
    }

    public String generateToken() {
        byte[] buffer = new byte[ACCESS_TOKEN_LENGTH];
        rand.nextBytes(buffer);
        return Base64.getEncoder().encodeToString(buffer);
    }
}
