package com.tosan.http.server.starter.annotation;

import java.lang.annotation.*;

/**
 * @author AmirHossein ZamanZade
 * @since 10/30/2022
 * <p>
 * Annotation for calculating the execution statistics of internal service calls
 * <br>
 * Statistics are obtained from this annotation will be logged after request completion
 * <br>
 * if server has a service that it calls some other services internally and the statistics of these service calls are needed ,
 * the method of these internal service call must be annotated with this annotation.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Timer {

    /**
     * @return service type for categorizing purpose
     */
    String serviceType() default "";

    /**
     * @return service name
     */
    String serviceName();
}
