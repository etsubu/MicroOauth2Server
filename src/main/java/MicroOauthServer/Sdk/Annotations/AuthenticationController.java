package MicroOauthServer.Sdk.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AuthenticationController annotation defines a controller for performing user authentication locally or trough remote
 * service
 * @author etsubu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AuthenticationController {
    String name() default "";
}