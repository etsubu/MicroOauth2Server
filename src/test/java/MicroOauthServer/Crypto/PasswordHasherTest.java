package MicroOauthServer.Crypto;

import MicroOauthServer.Crypto.PasswordHasherManager;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Random;

import static junit.framework.TestCase.*;

public class PasswordHasherTest {
    @Test
    public void testValidHashes() throws InvalidKeySpecException, NoSuchAlgorithmException {
        Random rand = new Random();
        int passwordsToGenerate = 5;
        PasswordHasherManager manager = new PasswordHasherManager();
        while (passwordsToGenerate-- > 0) {
            byte[] bytes = new byte[rand.nextInt(8) + 8];
            rand.nextBytes(bytes);
            String password = Base64.getEncoder().encodeToString(bytes);
            assertTrue(manager.validatePassword(manager.hash(password), password));
        }
    }

    @Test
    public void testInvalidHashes() throws InvalidKeySpecException, NoSuchAlgorithmException {
        Random rand = new Random();
        int passwordsToGenerate = 5;
        PasswordHasherManager manager = new PasswordHasherManager();
        while (passwordsToGenerate-- > 0) {
            byte[] bytes = new byte[rand.nextInt(8) + 8];
            rand.nextBytes(bytes);
            String password = Base64.getEncoder().encodeToString(bytes);
            String hash = manager.hash(password);
            // Change one byte in password
            bytes[rand.nextInt(bytes.length)]++;
            password = Base64.getEncoder().encodeToString(bytes);
            assertFalse(manager.validatePassword(hash, password));
        }
    }
}
