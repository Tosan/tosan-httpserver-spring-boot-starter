package com.tosan.http.server.starter.annotation;

import java.lang.annotation.*;

/**
 * Annotation for calculate the execution statistics of internal service calls and log them
 * <br>
 * if server has a service that it calls some other services internally and the statistics of these service call are needed ,
 * the method of these internal service call must be annotated with this annotation.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Timer {

    /**
     * @return Name of the webservice that is going to call internal service from it
     */
    String serviceType();

    /**
     * @return Name of the internal service that is going to call
     */
    String serviceName();
}
