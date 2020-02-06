package MicroOauthServer.Crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class PasswordHasherManager {
    private Logger log = LoggerFactory.getLogger(PasswordHasherManager.class);
    private SecureRandom rand;
    private Map<String, PasswordHasher> hashers;
    private PasswordHasher defaultHasher;

    public PasswordHasherManager() {
        rand = new SecureRandom();
        this.hashers = new HashMap<>();
        defaultHasher = new PBKDF2Hasher();
        hashers.put(PBKDF2Hasher.HASHER_NAME, defaultHasher);
        log.info("Initialized PasswordHasherManager");
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

    public String hash(String password) {
        try {
            return defaultHasher.hash(password);
        } catch (Exception e) {
            log.error("Failed to hash password ", e);
            throw new RuntimeException("Failed to hash password ", e);
        }
    }

    public boolean validatePassword(String hash, String password) {
        String[] parts = hash.split(":");
        if(parts.length > 0) {
            PasswordHasher hasher = hashers.get(parts[0]);
            if(hasher == null)
                throw new IllegalArgumentException("PasswordHasher was not found " + parts[0]);
            try {
                return hasher.validate(hash, password);
            } catch (Exception e) {
                log.error("Failed to hash password ", e);
                throw new RuntimeException("Failed to hash password ", e);
            }
        }
        throw new IllegalArgumentException("Invalid password hash format");
    }

}
