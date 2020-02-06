import MicroOauthServer.Crypto.SymmetricCrypto;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class SymmetricCryptoTest {

    @Test
    public void testCrypto() throws Exception {
        SymmetricCrypto crypto = new SymmetricCrypto();
        String plain = "test";
        assertEquals(plain, new String(crypto.decrypt(crypto.encrypt(plain.getBytes()))));
    }
}
