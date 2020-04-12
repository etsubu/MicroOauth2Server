package MicroOauthServer.Common;

import MicroOauthServer.Common.URLParser;
import org.junit.Test;

import java.util.Map;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class URLParserTest {
    private static final String BASE_PATH = "http://localhost/authorize";

    @Test
    public void testInvalidUrls() {
        assertTrue(URLParser.parseURLParameters(BASE_PATH).isEmpty());
        assertTrue(URLParser.parseURLParameters(BASE_PATH + "?").isEmpty());
    }

    @Test
    public void testValidUrls() {
        assertEquals(1, URLParser.parseURLParameters(BASE_PATH + "?asd").size());
        assertEquals(1, URLParser.parseURLParameters(BASE_PATH + "?asd=").size());
        assertEquals(1, URLParser.parseURLParameters(BASE_PATH + "?asd=dsa").size());
        assertEquals(1, URLParser.parseURLParameters(BASE_PATH + "?asd=dsa&").size());
        assertEquals(2, URLParser.parseURLParameters(BASE_PATH + "?asd=dsa&dsa").size());
        assertEquals(2, URLParser.parseURLParameters(BASE_PATH + "?asd=dsa&temp=").size());
        assertEquals(2, URLParser.parseURLParameters(BASE_PATH + "?asd=dsa&temp=dd").size());

        Map<String, String> params = URLParser.parseURLParameters(BASE_PATH + "?asd=dsa&temp=value");
        assertEquals("dsa", params.get("asd"));
        assertEquals("value", params.get("temp"));
    }

    @Test
    public void fuzzTest() {
        Random rand = new Random();
        for(int i = 0; i < 100000; i++) {
            byte[] data = new byte[20 + rand.nextInt(128)];
            String str = new String(data);
            assertTrue(URLParser.parseURLParameters(str).isEmpty());
        }
    }
}
