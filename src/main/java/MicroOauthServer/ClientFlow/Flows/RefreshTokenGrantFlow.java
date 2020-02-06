package MicroOauthServer.ClientFlow.Flows;

import MicroOauthServer.ClientDatabase.InvalidScopeException;
import MicroOauthServer.Exceptions.MicroOauthCoreException;
import MicroOauthServer.Token.TokenExpiredException;
import MicroOauthServer.Token.TokenService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
public class RefreshTokenGrantFlow extends GrantFlow {
    public static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";

    @Autowired
    private TokenService tokenService;

    @Autowired
    private Gson gson;

    @Override
    public String doFlow(Map<String, String> body, String authorization) throws MicroOauthCoreException, TokenExpiredException, InvalidScopeException {
        String refreshToken = body.get(REFRESH_TOKEN_GRANT_TYPE);
        String scope = body.get("scope");
        if(refreshToken == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is missing");
        return gson.toJson(tokenService.useRefreshToken(refreshToken, scope));
    }
}
