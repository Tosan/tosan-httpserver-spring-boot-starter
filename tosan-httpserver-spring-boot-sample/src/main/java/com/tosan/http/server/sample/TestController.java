package com.tosan.http.server.sample;

import com.tosan.http.server.sample.dto.TestRequestDto;
import com.tosan.http.server.sample.dto.TestResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author mina khoshnevisan
 * @since 7/12/2022
 */
@RestController
@RequestMapping("/httpserver")
@Slf4j
public class TestController {

    @PostMapping(value = "/test",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TestResponseDto testService(@RequestBody TestRequestDto dto) {
        TestResponseDto testResponseDto = new TestResponseDto();
        testResponseDto.setSecretKey("secret");
        testResponseDto.setPassword("954595");
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
}