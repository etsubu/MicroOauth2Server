package MicroOauthServer.Configuration;

import MicroOauthServer.ClientDatabase.ClientStorageAPI;
import MicroOauthServer.ClientDatabase.SimpleClientStorage;
import MicroOauthServer.ClientDatabase.SqlClientStorageController;
import MicroOauthServer.CredentialValidator.CredentialValidator;
import MicroOauthServer.CredentialValidator.SimpleCredentialValidator;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class Beans {
    private static final Logger log = LoggerFactory.getLogger(Beans.class);

    @Autowired
    private MicroOauthServer.Configuration.Configuration configuration;

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory()
    {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
        factory.setPort(configuration.getJettyConfig().getPort());
        factory.setContextPath("");
        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/not_found.html"));
        return factory;
    }

    @Bean
    public Gson createGsonBean() {
        return new Gson();
    }

    @Bean
    public CredentialValidator createCredentialValidator() {
        return new SimpleCredentialValidator();
    }

    @Bean
    public ClientStorageAPI createClientStorageApi() {
        if(configuration.getClientDatabase().getControllerName().equals("SQL")) {
            log.info("Initializing SQL database for oauth client storage");
            return new SqlClientStorageController(configuration);
        }
        return new SimpleClientStorage();
    }

}
