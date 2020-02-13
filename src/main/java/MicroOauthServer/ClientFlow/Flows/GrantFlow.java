package MicroOauthServer.ClientFlow.Flows;

import MicroOauthServer.ClientDatabase.InvalidScopeException;
import MicroOauthServer.Exceptions.MicroOauthCoreException;
import MicroOauthServer.Exceptions.TokenExpiredException;

import java.util.Map;

public abstract class GrantFlow {
    private String grantType;

    public abstract String doFlow(Map<String, String> body, String authorization) throws MicroOauthCoreException, TokenExpiredException, InvalidScopeException;
}
