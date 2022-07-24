package com.tosan.http.server.sample;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author mina khoshnevisan
 * @since 7/5/2022
 */
@SpringBootApplication
public class TestHttpServerUtilApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder app = new SpringApplicationBuilder(TestHttpServerUtilApplication.class)
                .web(WebApplicationType.SERVLET);
        app.run();
    }
}