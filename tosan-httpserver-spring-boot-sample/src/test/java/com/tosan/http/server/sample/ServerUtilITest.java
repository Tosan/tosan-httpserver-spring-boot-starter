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
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
    public void testService() {
        TestRequestDto dto = new TestRequestDto();
        dto.setTest("testValue");
        dto.setPan("4039484849393094");
        dto.setName("exceptionTest");
        dto.setFamily("kh");
        dto.setDate(new Date());
        dto.setLocalDate(LocalDate.now());
        dto.setMobileNumber("0984347384");
        setHeader();
        TestResponseDto testResponseDto = this.restTemplate.postForObject("http://localhost:" + port +
                "/httpserver/test", dto, TestResponseDto.class, new HashMap<>());
    }

    @Test
    public void testGetMethod() {
        this.restTemplate.getForObject("http://localhost:" + port +
                "/httpserver/testGet", Object.class, new HashMap<>());
    }

    @Test
    public void testFormURlEncodeService() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("PRIVATE-TOKEN", "xyz");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("parameter1", "feature");
        map.add("parameter2", "#5843AD");
        map.add("secretKey", "#548534953939");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<Object> response =
                restTemplate.exchange("/httpserver/confirm",
                        HttpMethod.POST,
                        entity,
                        Object.class);
    }

    @Test
    public void testRequestParams() {
        this.restTemplate.getForObject("http://localhost:" + port +
                "/httpserver/testRequestParams?name=mina&secretKey=kh", Object.class, new HashMap<>());
    }

    @Test
    public void testBodyAndRequestParam() {
        TestRequestDto dto = new TestRequestDto();
        dto.setTest("testValue");
        dto.setPan("4039484849393094");
        dto.setName("mina");
        dto.setFamily("kh");
        dto.setDate(new Date());
        setHeader();
        TestResponseDto testResponseDto = this.restTemplate.postForObject("http://localhost:" + port +
                "/httpserver/testBodyAndRequestParam?name=mina&secretKey=kh", dto, TestResponseDto.class, new HashMap<>());
    }

    @Test
    public void testMethodWithNoArgs() {
        this.restTemplate.getForObject("http://localhost:" + port +
                "/httpserver/noArgTest", Object.class, new HashMap<>());
    }

    @Test
    public void testTextContent() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setAccept(new ArrayList<MediaType>() {{
            add(MediaType.TEXT_PLAIN);
        }});
        HttpEntity<String> entity = new HttpEntity<>("input text value", headers);
        ResponseEntity<String> response =
                restTemplate.exchange("/httpserver/text",
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    @Test
    public void testGenerateReport() {
        this.restTemplate.getForObject("http://localhost:" + port +
                "/httpserver/generateReport", Object.class, new HashMap<>());
    }

    @Test
    public void testGenericReport() {
        this.restTemplate.getForObject("http://localhost:" + port +
                "/httpserver/genericReport", Object.class, new HashMap<>());
    }

    @Test
    public void testGetDepositInformation() {
        this.restTemplate.getForObject("http://localhost:" + port +
                "/httpserver/deposit/info/847483983", Object.class, new HashMap<>());
    }

    @Test
    public void testGetHttpStatusCode() {
        this.restTemplate.getForObject("http://localhost:" + port +
                "/httpserver/status", Object.class, new HashMap<>());
    }

    @Test
    public void testCollectionResponseBody() {
        this.restTemplate.getForObject("http://localhost:" + port +
                "/httpserver/getInfoList", Object.class, new HashMap<>());
    }

    @Test
    public void testChangeUsername() {
        this.restTemplate.getForObject("http://localhost:" + port +
                "/httpserver/changeUsername", Object.class, new HashMap<>());
    }

    @Test
    public void testInternalStatistics() {
        this.restTemplate.getForObject("http://localhost:" + port + "/httpserver/internalStatistics",
                Object.class, new HashMap<>());
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