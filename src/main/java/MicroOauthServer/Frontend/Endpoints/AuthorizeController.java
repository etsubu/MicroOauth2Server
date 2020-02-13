package MicroOauthServer.Frontend.Endpoints;

import MicroOauthServer.ClientDatabase.InvalidScopeException;
import MicroOauthServer.ClientFlow.Flows.GrantFlow;
import MicroOauthServer.ClientFlow.Flows.ImplicitGrantFlow;
import MicroOauthServer.Common.URLParser;
import MicroOauthServer.CredentialValidator.CredentialValidator;
import MicroOauthServer.Exceptions.MicroOauthCoreException;
import MicroOauthServer.Sdk.Annotations.RequireScopes;
import MicroOauthServer.Exceptions.TokenExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthorizeController {
    private static final Logger log = LoggerFactory.getLogger(AuthorizeController.class);
    public static final String LOGIN_TEMPLATE = "login.html";
    public static final String REDIRECT_URI = "redirect_uri";

    @Autowired
    private CredentialValidator validator;

    @Autowired
    private ImplicitGrantFlow implicitGrantFlow;

    private Map<String, GrantFlow> grantFlows;

    @PostConstruct
    public void init() {
        grantFlows = new HashMap<>();
        grantFlows.put(ImplicitGrantFlow.TOKEN, implicitGrantFlow);
    }

    @RequestMapping(value = "/authorize", method = RequestMethod.GET)
    public String authorizationRequest(@RequestParam(value = "response_type") String grantType,
                                             @RequestParam(value = "client_id") String clientId,
                                             @RequestParam(value = "state") String state,
                                             @RequestParam(value = "redirect_uri") String redirectUri) {

        return LOGIN_TEMPLATE;
    }

    private void handlePostAuthenticate(HttpServletRequest request, HttpServletResponse response, String referrer) throws IOException, InvalidScopeException, TokenExpiredException, MicroOauthCoreException {
        Map<String, String> params = URLParser.parseURLParameters(referrer);
        String type = params.get("response_type");
        if(type != null) {
            GrantFlow flow = grantFlows.get(type);
            if(flow != null) {
                response.sendRedirect(flow.doFlow(params, null));
            }
        }
    }

    @RequestMapping(value = "/authorize", method = RequestMethod.POST)
    public String authenticationPost(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestParam(required = true) String username,
                                     @RequestParam(required = true) String password) throws IOException {
        String referrer = request.getHeader("referer");
        if(referrer == null || username == null || password == null) {
            log.info("Post request missing parameters");
        } else {
            log.info("Received authorize post" );
            if(validator.authenticate(username, password, null)) {
                try {
                    handlePostAuthenticate(request, response, referrer);
                } catch (MicroOauthCoreException e) {

                } catch (InvalidScopeException e) {

                } catch (TokenExpiredException e) {

                }
            }
        }
        return null;
    }
}
