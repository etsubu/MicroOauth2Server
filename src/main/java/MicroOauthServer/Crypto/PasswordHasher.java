package MicroOauthServer.Crypto;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface PasswordHasher {
    String hash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException;
    boolean validate(String hash, String password) throws NoSuchAlgorithmException, InvalidKeySpecException;
}
