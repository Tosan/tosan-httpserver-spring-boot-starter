package com.tosan.http.server.sample;

import com.tosan.http.server.sample.dto.TestRequestDto;
import com.tosan.http.server.sample.dto.TestResponseDto;
import com.tosan.http.server.starter.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.util.Collections;
import java.util.HashMap;

/**
 * @author mina khoshnevisan
 * @since 7/16/2022
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServerUtilITest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        restTemplate.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler());
    }

    @Test
    public void greetingShouldReturnDefaultMessage() {
        TestRequestDto dto = new TestRequestDto();
        dto.setTest("testValue");
        dto.setPan("4039484849393094");
        dto.setName("mina");
        dto.setFamily("kh");
        setHeader();
        TestResponseDto testResponseDto = this.restTemplate.postForObject("http://localhost:" + port +
                "/httpserver/test", dto, TestResponseDto.class, new HashMap<>());
        System.out.println("testResponseDto = " + testResponseDto);
    }

    public void setHeader() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
//                    request.getHeaders().add(X_REQUEST_ID, "val453453ue");
                    request.getHeaders().add(Constants.X_USER_IP, "192.168.16.23");
                    request.getHeaders().add(Constants.X_FORWARDED_FOR, "192.168.16.49,192.168.16.50");
                    request.getHeaders().add("username", "mina948j");
                    request.getHeaders().add("context", "{\"secretKey\":\"456677\", \"test\":\"minaName\"}");
                    return execution.execute(request, body);
                }));
    }
}