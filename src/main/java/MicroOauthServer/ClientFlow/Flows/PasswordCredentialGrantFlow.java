package MicroOauthServer.ClientFlow.Flows;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PasswordCredentialGrantFlow extends GrantFlow {
    public static final String PASSWORD_GRANT_TYPE = "password";

    @Override
    public String doFlow(Map<String, String> body, String authorization) {
        return null;
    }
}
