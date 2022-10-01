package com.tosan.http.server.sample;

import com.tosan.http.server.sample.dto.ResultSetModel;
import com.tosan.http.server.sample.dto.TestRequestDto;
import com.tosan.http.server.sample.dto.TestResponseDto;
import com.tosan.http.server.sample.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mina khoshnevisan
 * @since 7/12/2022
 */
@RestController
@RequestMapping("/httpserver")
@Slf4j
public class TestRestController {

    @PostMapping(value = "/test",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TestResponseDto testService(@RequestBody TestRequestDto dto) {
        TestResponseDto testResponseDto = new TestResponseDto();
        testResponseDto.setSecretKey("secret");
        testResponseDto.setPassword("954595");
        if (dto.getName().equals("exceptionTest")) {
            throw new CustomException("mina test exception", "45345345");
        }
        return testResponseDto;
    }

    @PostMapping(value = "/confirm", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @ResponseStatus(value = HttpStatus.FOUND)
    public void testUrlEncodedForm(HttpServletRequest request) {
        String parameter1 = request.getParameter("parameter1");
        System.out.println("parameter1 = " + parameter1);
        String parameter2 = request.getParameter("parameter2");
        System.out.println("parameter2 = " + parameter2);
    }

    @GetMapping(value = "/testGet")
    @ResponseStatus(value = HttpStatus.OK)
    public void testGetMethod() {
        System.out.println("get business here");
    }

    @GetMapping(value = "/testRequestParams")
    public void testRequestParams(@RequestParam("name") String name, @RequestParam("secretKey") String secretKey) {
        System.out.println("request params received from request");
    }

    @PostMapping(value = "/testBodyAndRequestParam")
    public TestResponseDto testBodyAndRequestParam(@RequestParam("name") String name,
                                                   @RequestParam("secretKey") String secretKey,
                                                   @RequestBody TestRequestDto dto) {
        TestResponseDto testResponseDto = new TestResponseDto();
        testResponseDto.setSecretKey("secret");
        testResponseDto.setPassword("954595");
        if (dto.getName().equals("exceptionTest")) {
            throw new CustomException("mina test exception", "45345345");
        }
        return testResponseDto;
    }

    @GetMapping(value = "/noArgTest")
    public TestResponseDto methodWithNoArgTest(HttpServletRequest request) {
        TestResponseDto testResponseDto = new TestResponseDto();
        testResponseDto.setSecretKey("secret");
        testResponseDto.setPassword("954595");
        return testResponseDto;
    }

    @PostMapping(value = "/text", consumes = {MediaType.TEXT_PLAIN_VALUE}, produces = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseStatus(value = HttpStatus.FOUND)
    public String testUrlEncodedForm(String text) {
        return "MINA kh";
    }

    @GetMapping(value = "/generateReport")
    @ResponseStatus(value = HttpStatus.OK)
    public void generateReport(HttpServletResponse response) {
        System.out.println(response.getStatus());
    }

    @GetMapping(value = "/genericReport")
    @ResponseStatus(value = HttpStatus.OK)
    public ResultSetModel<String> genericReport() {
        List<String> datasource = new ArrayList<>();
        datasource.add("testData");
        ResultSetModel<String> resultSetModel = new ResultSetModel<>(datasource, 1);
        return resultSetModel;
    }

    @GetMapping(value = "/deposit/info/{depositNumber}")
    @ResponseStatus(value = HttpStatus.OK)
    public void getDepositInformation(@PathVariable String depositNumber) {
        System.out.println("depositNumber = " + depositNumber);
    }

    @GetMapping(value = "/status")
    @ResponseStatus(value = HttpStatus.OK)
    public HttpStatus getStatus() {
        return HttpStatus.OK;
    }

    @GetMapping(value = "/getInfoList")
    @ResponseStatus(value = HttpStatus.OK)
    public List<TestResponseDto> testCollectionResponseBody() {
        List<TestResponseDto> responseDtoList = new ArrayList<>();
        TestResponseDto testResponseDto = new TestResponseDto();
        testResponseDto.setPassword("4837");
        testResponseDto.setSecretKey("mina9384");
        responseDtoList.add(testResponseDto);
        return responseDtoList;
    }

    @GetMapping(value = "/changeUsername")
    public boolean changeUserName() {
        return false;
    }
}