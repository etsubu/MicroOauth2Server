package MicroOauthServer.Interceptors;

import MicroOauthServer.ClientDatabase.ClientManager;
import MicroOauthServer.ClientDatabase.InvalidScopeException;
import MicroOauthServer.Sdk.Annotations.RequireScopes;
import MicroOauthServer.Token.IntrospectionResponse;
import MicroOauthServer.Token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * AccessManagementInterceptor validates the clients authorization when requested protected resources with oauth2 scope
 * requirements. Authorization token is validated and the granted scopes verified against the requested resources
 * requirements. If no proper authorization for the clients token is granted the interceptor return 401 UNAUTHORIZED
 * @author etsubu
 */
@Component
public class AccessManagementInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(AccessManagementInterceptor.class);

    @Autowired
    private TokenService tokenService;

    /**
     * Validate a bearer token and its scopes against the required scopes of the protected resource
     * @param authorization Authorization field of http request from client
     * @param requiredScopes Scopes this resource requires
     * @return True if access should be granted, false if rejected
     */
    private boolean validateAuthorization(String authorization, String requiredScopes) {
        if(authorization.length() <= 7)
            return false;
        int index = authorization.indexOf("Bearer ");
        if(index == -1)
            return false;
        String token = authorization.substring(7);
        log.info("Validating access to protected resource " +token);
        try {
            IntrospectionResponse response = tokenService.introspectToken(token);
            return response.getActive() && !ClientManager.validateScopes(response.getScope(), requiredScopes).isEmpty();
        } catch (InvalidScopeException e) {
            log.error("", e);
            return false;
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            // Verify that the resource is protected
            if(method.getMethod().isAnnotationPresent(RequireScopes.class)) {
                RequireScopes scopes = method.getMethod().getDeclaredAnnotation(RequireScopes.class);
                String requiredScopes = scopes.scopes();
                boolean redirectToAuthorize = scopes.redirectToAuthorize();
                // Only validate authorization if some scope requirement is present
                if(!requiredScopes.isEmpty()) {
                    String authorization = request.getHeader("Authorization");
                    // Does the client have authorization to access the resource
                    if (authorization == null || !validateAuthorization(authorization, requiredScopes)) {
                        if(redirectToAuthorize && request.getMethod().equals("GET")) {
                            try {
                                response.sendRedirect("http://localhost/authorize?client_id=internal&response_type=token&state=123&redirect_uri=" + URLEncoder.encode(request.getRequestURL().toString(), StandardCharsets.UTF_8));
                            } catch (IOException e) {
                                log.error("Failed to send redirect for the client ", e);
                            }
                        } else {
                            log.info("Rejecting access to protected resource");
                            try {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized request");
                            } catch (Exception e) {
                                log.error("Failed to set http response to UNAUTHORIZED ", e);
                            }
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
