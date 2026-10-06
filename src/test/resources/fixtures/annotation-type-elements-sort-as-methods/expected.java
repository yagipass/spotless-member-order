package fixtures;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Endpoint {
    enum Method {
        GET, POST
    }

    @interface Header {
        String name();
    }

    int DEFAULT_TIMEOUT = 30;

    String path();

    Method method() default Method.GET;

    String[] produces() default {"application/json"};

    int timeout() default DEFAULT_TIMEOUT;
}
