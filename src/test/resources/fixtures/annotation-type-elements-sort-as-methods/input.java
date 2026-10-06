package fixtures;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Endpoint {
    String path();

    enum Method {
        GET, POST
    }

    Method method() default Method.GET;

    int DEFAULT_TIMEOUT = 30;

    String[] produces() default {"application/json"};

    @interface Header {
        String name();
    }

    int timeout() default DEFAULT_TIMEOUT;
}
