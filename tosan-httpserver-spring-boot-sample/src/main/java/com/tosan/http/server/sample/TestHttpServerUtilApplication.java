package com.tosan.http.server.sample;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author mina khoshnevisan
 * @since 7/5/2022
 */
@SpringBootApplication
@EnableWebMvc
public class TestHttpServerUtilApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder app = new SpringApplicationBuilder(TestHttpServerUtilApplication.class)
                .web(WebApplicationType.SERVLET);
        app.run();
    }
}