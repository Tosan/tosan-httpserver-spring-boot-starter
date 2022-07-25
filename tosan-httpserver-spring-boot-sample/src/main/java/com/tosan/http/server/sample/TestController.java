package com.tosan.http.server.sample;

import com.tosan.http.server.sample.dto.TestRequestDto;
import com.tosan.http.server.sample.dto.TestResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}