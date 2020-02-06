package Crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class PasswordHasherManager {
    private SecureRandom rand;
    private Map<String, PasswordHasher> hashers;
    private PasswordHasher defaultHasher;

    public PasswordHasherManager() {
        rand = new SecureRandom();
        this.hashers = new HashMap<>();
        defaultHasher = new PBKDF2Hasher();
        hashers.put(PBKDF2Hasher.HASHER_NAME, defaultHasher);
    }

    private String generateHash(String hasherName, String password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        PasswordHasher hasher = hashers.get(hasherName);
        if(hasher == null)
            throw new NoSuchAlgorithmException("No such password hasher with name " + hasherName);
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = new byte[16];
        rand.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return "{PBKDF2WithHmacSHA1}:" + iterations + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public String hash(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return defaultHasher.hash(password);
    }

    public boolean validatePassword(String hash, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String[] parts = hash.split(":");
        if(parts.length > 0) {
            PasswordHasher hasher = hashers.get(parts[0]);
            if(hasher == null)
                throw new IllegalArgumentException("PasswordHasher was not found " + parts[0]);
            return hasher.validate(hash, password);
        }
        throw new IllegalArgumentException("Invalid password hash format");
    }

}
