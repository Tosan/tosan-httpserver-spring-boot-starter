package com.tosan.http.server.sample;

import com.tosan.http.server.sample.dto.TestRequestDto;
import com.tosan.http.server.sample.dto.TestResponseDto;
import com.tosan.http.server.starter.util.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author mina khoshnevisan
 * @since 7/16/2022
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class ServerUtilITest {

    @Autowired
    private RestTestClient restTestClient;

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
        dto.setTestNumber(84874);
        dto.setAmount(new BigDecimal("49400000"));
        dto.setAge((short) 45);
        dto.setAverage(3.56444);
        dto.setLength(400000);
        TestResponseDto testResponseDto = this.restTestClient.post()
                .uri("/httpserver/test")
                .headers(this::addCommonHeaders)
                .body(dto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(TestResponseDto.class)
                .getResponseBody();
    }

    @Test
    public void testGetMethod() {
        this.restTestClient.get()
                .uri("/httpserver/testGet")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void testFormURlEncodeService() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("parameter1", "feature");
        map.add("parameter2", "#5843AD");
        map.add("secretKey", "#548534953939");

        this.restTestClient.post()
                .uri("/httpserver/confirm")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("PRIVATE-TOKEN", "xyz")
                .body(map)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void testRequestParams() {
        this.restTestClient.get()
                .uri("/httpserver/testRequestParams?name=mina&secretKey=kh")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void testBodyAndRequestParam() {
        TestRequestDto dto = new TestRequestDto();
        dto.setTest("testValue");
        dto.setPan("4039484849393094");
        dto.setName("mina");
        dto.setFamily("kh");
        dto.setDate(new Date());
        TestResponseDto testResponseDto = this.restTestClient.post()
                .uri("/httpserver/testBodyAndRequestParam?name=mina&secretKey=kh")
                .headers(this::addCommonHeaders)
                .body(dto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(TestResponseDto.class)
                .getResponseBody();
    }

    @Test
    public void testMethodWithNoArgs() {
        this.restTestClient.get()
                .uri("/httpserver/noArgTest")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void testTextContent() {
        this.restTestClient.post()
                .uri("/httpserver/text")
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .body("input text value")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void testGenerateReport() {
        this.restTestClient.get()
                .uri("/httpserver/generateReport")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void testGenericReport() {
        this.restTestClient.get()
                .uri("/httpserver/genericReport")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void testGetDepositInformation() {
        this.restTestClient.get()
                .uri("/httpserver/deposit/info/847483983")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void testGetHttpStatusCode() {
        this.restTestClient.get()
                .uri("/httpserver/status")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void testCollectionResponseBody() {
        this.restTestClient.get()
                .uri("/httpserver/getInfoList")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void testChangeUsername() {
        this.restTestClient.get()
                .uri("/httpserver/changeUsername")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void testInternalStatistics() {
        this.restTestClient.get()
                .uri("/httpserver/internalStatistics")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    private void addCommonHeaders(HttpHeaders headers) {
        headers.add(Constants.X_USER_IP, "192.168.16.23");
        headers.add(Constants.X_FORWARDED_FOR, "192.168.16.49,192.168.16.50");
        headers.add("username", "mina948j");
        headers.add("context", "{\"secretKey\":\"456677\", \"test\":\"minaName\"}");
        headers.add("x-api-key", "\"7657443\"");
    }
}