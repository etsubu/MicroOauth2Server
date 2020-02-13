package MicroOauthServer.Exceptions;

public class InvalidRedirectUriException extends Exception {
    public InvalidRedirectUriException(){
        super();
    }

    public InvalidRedirectUriException(String message) {
        super(message);
    }
}
