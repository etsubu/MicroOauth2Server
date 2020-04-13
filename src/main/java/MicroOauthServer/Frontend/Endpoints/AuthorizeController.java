package MicroOauthServer.Frontend.Endpoints;

import MicroOauthServer.Authentication.AuthenticationController;
import MicroOauthServer.ClientDatabase.ClientAuthenticationException;
import MicroOauthServer.ClientDatabase.InvalidScopeException;
import MicroOauthServer.ClientFlow.Flows.GrantFlow;
import MicroOauthServer.ClientFlow.Flows.ImplicitGrantFlow;
import MicroOauthServer.Common.URLParser;
import MicroOauthServer.CredentialValidator.CredentialValidator;
import MicroOauthServer.Exceptions.InvalidClientException;
import MicroOauthServer.Exceptions.MicroOauthCoreException;
import MicroOauthServer.Exceptions.UnsupportedGrantTypeException;
import MicroOauthServer.Sdk.Annotations.RequireScopes;
import MicroOauthServer.Exceptions.TokenExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    private AuthenticationController authenticator;

    @Autowired
    private ImplicitGrantFlow implicitGrantFlow;

    private Map<String, GrantFlow> grantFlows;

    @PostConstruct
    public void init() {
        grantFlows = new HashMap<>();
        grantFlows.put(ImplicitGrantFlow.TOKEN, implicitGrantFlow);
    }

    private String handlePostAuthenticate(HttpServletRequest request, HttpServletResponse response) throws IOException,
            InvalidScopeException, TokenExpiredException, MicroOauthCoreException, InvalidClientException, ClientAuthenticationException, UnsupportedGrantTypeException {
        Map<String, String> params = URLParser.parseURLParameters("?" + request.getQueryString());
        System.out.println(request.getQueryString());
        String type = params.get("response_type");
        if(type != null) {
            GrantFlow flow = grantFlows.get(type);
            if(flow != null) {
                response.sendRedirect(flow.doFlow(params, null));
                return "redirect:" + flow.doFlow(params, null);
            }
        }
        throw new UnsupportedGrantTypeException("Client requested invalid grant type");
    }


    @RequestMapping(value = "/authorize", method = RequestMethod.GET)
    public String authorizationRequest(@RequestParam(value = "response_type") String grantType,
                                       @RequestParam(value = "client_id") String clientId,
                                       @RequestParam(value = "state") String state,
                                       @RequestParam(value = "redirect_uri") String redirectUri) throws UnsupportedGrantTypeException {
        log.info(grantType);
        if(!grantFlows.containsKey(grantType)) {
            log.info("Client requested invalid grant type");
            throw new UnsupportedGrantTypeException();
        }
        return LOGIN_TEMPLATE;
    }

    @RequestMapping(value = "/authorize", method = RequestMethod.POST)
    public String authenticationPost(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestParam(required = true) String username,
                                     @RequestParam(required = true) String password,
                                     @RequestParam(value = "response_type") String grantType,
                                     @RequestParam(value = "client_id") String clientId,
                                     @RequestParam(value = "state") String state,
                                     @RequestParam(value = "redirect_uri") String redirectUri) throws IOException, UnsupportedGrantTypeException {
        log.info("Authentication for {}:{}", username, password);
        if(!grantFlows.containsKey(grantType)) {
            log.info("Client requested invalid grant type");
            throw new UnsupportedGrantTypeException();
        } else {
            log.info("Received authorize post" );
            if(authenticator.authenticate(username, password.toCharArray()).isAuthenticated()) {
                log.info("Authenticated successfully");
                try {
                    return handlePostAuthenticate(request, response);
                } catch (MicroOauthCoreException | InvalidScopeException | TokenExpiredException | InvalidClientException | ClientAuthenticationException e) {
                    e.printStackTrace();
                }
            }
        }
        // Invalid login. Redirect back to login page
        String samePath = request.getRequestURI() + "?" + request.getQueryString();
        return "redirect:" + samePath;
    }
}
