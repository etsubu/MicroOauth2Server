package Frontend.Configuration;

import ClientFlow.ClientFlowI;
import ClientFlow.SimpleClientFlow;
import Token.TokenGenerator;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {
    @Bean
    public ConfigurableServletWebServerFactory webServerFactory()
    {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
        factory.setPort(80);
        factory.setContextPath("");
        factory.addErrorPages(new ErrorPage("/notfound.html"));
        return factory;
    }

    @Bean
    public Gson createGsonBean() {
        return new Gson();
    }

    @Bean
    public ClientFlowI clientFlow() {
        return new SimpleClientFlow();
    }

    @Bean
    public TokenGenerator tokenGenerator() {
        return new TokenGenerator();
    }
}
