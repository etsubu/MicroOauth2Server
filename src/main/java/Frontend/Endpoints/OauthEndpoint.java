package Frontend.Endpoints;

import ClientFlow.ClientFlowI;
import Token.AuthorizationToken;
import Token.BearerToken;
import Token.TokenGenerator;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
public class OauthEndpoint {
    @Autowired
    private ClientFlowI clientFlow;

    @Autowired
    private TokenGenerator tokenGenerator;

    @PostMapping(value = "/token")
    public AuthorizationToken clientLoginFlow(@RequestParam(value = "grant_type") String grantType,
                                              @RequestParam(value = "client_id") String clientId,
                                              @RequestParam(value = "client_secret") String secret,
                                              @RequestParam(value = "audience") String audience) {
        return new BearerToken(tokenGenerator.generateToken(), 10);
    }

    @GetMapping(value = "/test")
    public AuthorizationToken asd() {
        return new BearerToken("dsadsa", 10);
    }
}
