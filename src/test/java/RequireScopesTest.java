import MicroOauthServer.Sdk.Annotations.RequireScopes;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RequireScopesTest {

    @RequireScopes(scopes = "access-resource")
    private void testMethod() {

    }

    @Test
    public void testAnnotation() {
        for(Method m : RequireScopesTest.class.getDeclaredMethods()) {
            System.out.println(m.getName() + " " + m.isAnnotationPresent(RequireScopes.class));
            if(m.isAnnotationPresent(RequireScopes.class)) {
                RequireScopes scopes = m.getDeclaredAnnotation(RequireScopes.class);
                System.out.println(scopes.scopes());
            }
        }
        System.out.println("end");
    }
}
