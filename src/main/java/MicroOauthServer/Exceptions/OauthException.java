package MicroOauthServer.Exceptions;

import com.google.gson.annotations.SerializedName;
import org.springframework.http.HttpStatus;

/**
 * Oauth Exception base class which handles proper json serializing of oauth exceptions.
 * Exception structure is the one defined in the RFC.
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-5.2"></a>
 * @author etsubu
 */
public class OauthException extends Exception {
    /**
     * REQUIRED.  A single ASCII [USASCII] error code from the following:
     * invalid_request, invalid_client, invalid_grant, unauthorized_client, unsupported_grant_type, invalid_scope
     */
    @SerializedName("error")
    private final String error;

    /**
     * OPTIONAL.  Human-readable ASCII [USASCII] text providing
     * additional information, used to assist the client developer in
     * understanding the error that occurred.
     */
    @SerializedName("error_description")
    private final String description;

    /**
     * OPTIONAL.  A URI identifying a human-readable web page with
     * information about the error, used to provide the client
     * developer with additional information about the error.
     */
    @SerializedName("error_uri")
    private final String errorUri;

    private final HttpStatus status;

    /**
     * Initializes OauthException
     * @param status HTTP status response
     * @param error Error type
     * @param description Human-readable error description
     * @param errorUri URI to documentation page describing the error
     */
    public OauthException(HttpStatus status, String error, String description, String errorUri) {
        this.status = status;
        this.error = error;
        this.description = description;
        this.errorUri = errorUri;
    }

    /**
     *
     * @return HTTP response status
     */
    public HttpStatus getStatus() { return status; }

    /**
     *
     * @return Error type
     */
    public String getError() { return error; }

    /**
     *
     * @return Human-readable error description
     */
    public String getDescription() { return description; }

    /**
     *
     * @return Error URI which contains more documentation about the error
     */
    public String getErrorUri() { return errorUri; }

    /**
     * Serializes OauthException to JSON
     * @return JSON string of the exception matching RFC error response structure
     */

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(64);
        builder.append("{\"error\":\"")
                .append(error)
                .append("\"");
        if(description != null) {
            builder.append(',')
                    .append("\"error_description\":\"")
                    .append(description)
                    .append("\"");
        }
        if(errorUri != null) {
            builder.append(',')
                    .append("\"error_uri\":\"")
                    .append(errorUri)
                    .append("\"");
        }
        builder.append('}');
        return builder.toString();
    }
}
