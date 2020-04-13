package MicroOauthServer.Frontend.Endpoints;

import MicroOauthServer.Exceptions.InvalidRequestException;
import MicroOauthServer.Exceptions.OauthException;
import com.google.gson.Gson;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/error")
public class OauthErrorController implements ErrorController {
    private final Gson gson;

    public OauthErrorController() {
        this.gson = new Gson();
    }
    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping
    public String error(HttpServletRequest request, HttpServletResponse response) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("org.springframework.web.servlet.DispatcherServlet.EXCEPTION");
        var attr = request.getAttributeNames();
        while(attr.hasMoreElements()) {
            String str = attr.nextElement();
            //System.out.println(str + ":" + request.getAttribute(str.toString()));
        }
        response.setStatus(statusCode);
        if(exception instanceof OauthException) {
            // Set http status
            response.setStatus(((OauthException)exception).getStatus().value());
            return exception.toString();
        } else if(exception instanceof MissingServletRequestParameterException) {
            return new InvalidRequestException((String)request.getAttribute("javax.servlet.error.message")).toString();
        }
        return gson.toJson(exception);
    }
}
