package MicroOauthServer.Crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

@Component
public class SymmetricCrypto {
    private static final Logger log = LoggerFactory.getLogger(SymmetricCrypto.class);
    public static final int AES_KEY_SIZE = 128;
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;
    private SecureRandom rand;
    private final byte[] key;

    public SymmetricCrypto() {

        this.rand = new SecureRandom();
        this.key = new byte[16];
        for(int i = 0; i < key.length; i++)
            key[i] = (byte)i;
        log.info("Initialized SymmetricCrypto service");
    }

    public byte[] encrypt(byte[] plaintext) throws Exception
    {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        byte[] IV = new byte[GCM_IV_LENGTH];
        rand.nextBytes(IV);

        // Create GCMParameterSpec
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

        // Perform Encryption
        byte[] cipherText = cipher.doFinal(plaintext);
        byte[] cipherTextWithIV = new byte[cipherText.length + IV.length];
        System.arraycopy(IV, 0, cipherTextWithIV, 0, IV.length);
        System.arraycopy(cipherText, 0, cipherTextWithIV, IV.length, cipherText.length);

        return cipherTextWithIV;
    }

    public byte[] decrypt(byte[] cipherText) throws Exception
    {
        if(cipherText.length <= GCM_IV_LENGTH)
            throw new IllegalArgumentException("Cipher text is too short");
        byte[] IV = Arrays.copyOf(cipherText, GCM_IV_LENGTH);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        // Create GCMParameterSpec
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

        // Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

        // Perform Decryption
        return cipher.doFinal(Arrays.copyOfRange(cipherText, GCM_IV_LENGTH, cipherText.length));
    }
}
