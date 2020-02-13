package MicroOauthServer.Common;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * URL manipulating tools
 * @author etsubu
 */
public class URLParser {

    /**
     * Parses parameters from an URL
     * @param url
     * @return
     */
    public static Map<String, String> parseURLParameters(String url) {
        Map<String, String> params = new HashMap<>();
        int index;
        if(url == null || (index = url.indexOf('?')) == -1 || index == url.length() - 1) {
            return params;
        }
        for( String kv : url.substring(index + 1).split("&") ) {
            int idx = kv.indexOf( '=' );
            if( idx != -1 && idx != kv.length() - 1) {
                String name = URLDecoder.decode( kv.substring( 0, idx ), StandardCharsets.UTF_8);
                String value = URLDecoder.decode( kv.substring( idx+1 ), StandardCharsets.UTF_8);
                params.put(name, value);
            }
            else {
                String name = URLDecoder.decode( kv, StandardCharsets.UTF_8 );
                params.put( name, "");
            }
        }
        return params;
    }
}
