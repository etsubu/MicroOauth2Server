package MicroOauthServer.Crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PBKDF2Hasher implements PasswordHasher {
    public static final String HASHER_NAME = "{PBKDF2WithHmacSHA1}";
    private SecureRandom rand;
    private int iterations;

    public PBKDF2Hasher() {
        rand = new SecureRandom();
        this.iterations = 1000;
    }

    public String hash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = new byte[16];
        rand.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return "{PBKDF2WithHmacSHA1}:" + iterations + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public boolean validate(String hashStr, String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
        String[] parts = hashStr.split(":");
        if(parts.length != 4) {
            throw new IllegalArgumentException("Invalid password hash format");
        }
        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] hash = Base64.getDecoder().decode(parts[3]);
            return MessageDigest.isEqual(hash, hash(password, salt, iterations));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid password hash format");
        }
    }

    private byte[] hash(String password, byte[] salt, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException
    {

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return skf.generateSecret(spec).getEncoded();
    }
}
