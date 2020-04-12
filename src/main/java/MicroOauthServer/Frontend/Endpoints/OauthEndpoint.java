package MicroOauthServer.Frontend.Endpoints;

import MicroOauthServer.ClientDatabase.ClientManager;
import MicroOauthServer.ClientDatabase.InvalidScopeException;
import MicroOauthServer.ClientFlow.Flows.ClientGrantFlow;
import MicroOauthServer.ClientFlow.Flows.GrantFlow;
import MicroOauthServer.ClientFlow.Flows.PasswordCredentialGrantFlow;
import MicroOauthServer.ClientFlow.Flows.RefreshTokenGrantFlow;
import MicroOauthServer.Exceptions.InvalidClientException;
import MicroOauthServer.Exceptions.MicroOauthCoreException;
import MicroOauthServer.Exceptions.TokenExpiredException;
import MicroOauthServer.Sdk.Annotations.RequireScopes;
import MicroOauthServer.Sdk.Annotations.Scopes.Scopes;
import MicroOauthServer.Token.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OauthEndpoint {
    private static final Logger log = LoggerFactory.getLogger(OauthEndpoint.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ClientManager manager;

    @Autowired
    private PasswordCredentialGrantFlow passwordCredentialGrantFlow;

    @Autowired
    private RefreshTokenGrantFlow refreshTokenGrantFlow;

    @Autowired
    private ClientGrantFlow clientGrantFlow;

    private final Map<String, GrantFlow> grantFlows;

    public OauthEndpoint() {
        grantFlows = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        grantFlows.put(PasswordCredentialGrantFlow.PASSWORD_GRANT_TYPE, passwordCredentialGrantFlow);
        grantFlows.put(RefreshTokenGrantFlow.REFRESH_TOKEN_GRANT_TYPE, refreshTokenGrantFlow);
        grantFlows.put(ClientGrantFlow.CLIENT_GRANT_TYPE, clientGrantFlow);
    }

    @GetMapping(value = "/healthcheck")
    @RequireScopes(scopes = "test-scope", redirectToAuthorize = true)
    public String healthcheck() {
        return "Alive";
    }

    @PostMapping(value = "/token")
    public String loginFlow(@RequestParam Map<String,String> body,
                                  @RequestParam(value = "grant_type") String grantType,
                                  @RequestHeader(value = "Authorization", required = false) String authorization) throws Exception{
        if(body == null)
            body = new HashMap<>(0);
        GrantFlow flow = grantFlows.get(grantType);
        if(flow == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid grant_type");
        }

        try {
            return flow.doFlow(body, authorization);
        }catch (MicroOauthCoreException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error. Please try again later");
        }
    }

    @PostMapping(value = "/introspect")
    @RequireScopes(scopes = Scopes.TOKEN_INTROSPECT)
    public IntrospectionResponse tokenIntrospect(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 @RequestParam(value = "token") String token,
                                                 @RequestParam(value = "token_type_hint", required = false) String tokenTypeHint) {
        return tokenService.introspectToken(token);
    }
}
