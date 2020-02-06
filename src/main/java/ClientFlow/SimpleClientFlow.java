package ClientFlow;

public class SimpleClientFlow implements ClientFlowI{
    @Override
    public boolean validateClientFlowRequest(String clientId, String clientSecret) {
        return clientId.equals("testClient") && clientSecret.equals("1234");
    }
}
