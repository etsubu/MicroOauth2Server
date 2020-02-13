package MicroOauthServer.Crypto;

import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Generates keystores with keys and loads keys from said keystores
 * @author etsubu
 */
public class KeyStoreGenerator {
    public static final String TOKEN_KEY_ALIAS = "token_key";
    public static final String KEYSTORE_TYPE = "PKCS12";

    public static boolean generateKeystore(Path keystorePath, char[] password)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(null, null); // Initialize a blank keystore

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // for example
        SecretKey symmetricKey = keyGen.generateKey();

        byte[] salt = new byte[20];
        new SecureRandom().nextBytes(salt);
        keyStore.setEntry(TOKEN_KEY_ALIAS, new KeyStore.SecretKeyEntry(symmetricKey),
                new KeyStore.PasswordProtection(password, "PBEWithHmacSHA512AndAES_128",
                        new PBEParameterSpec(salt, 100_000)));

        keyStore.store(new FileOutputStream(keystorePath.toFile()), password);
        return true;
    }

    public static SecretKey loadSymmetricKey(Path keystorePath, char[] password)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(password);
        try(InputStream keyStoreData = new FileInputStream(keystorePath.toFile())) {
            keyStore.load(keyStoreData, password);
            return (SecretKey) keyStore.getEntry(TOKEN_KEY_ALIAS, entryPassword);
        }
    }
}
