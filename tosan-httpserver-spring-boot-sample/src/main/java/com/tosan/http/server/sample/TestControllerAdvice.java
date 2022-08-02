package com.tosan.http.server.sample;

import com.tosan.http.server.sample.dto.HttpServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mina khoshnevisan
 * @since 8/2/2022
 */
@RestControllerAdvice
@Slf4j
public class TestControllerAdvice {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<HttpServerException> handleThrowable(Throwable throwable) {
        log.error("Exception: " + throwable.toString() + "\n", throwable);
        HttpServerException httpServerException = extractHttpServerException(throwable, new HashMap<>());
        return new ResponseEntity<>(httpServerException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private HttpServerException extractHttpServerException(
            Throwable throwable, Map<String, Object> exceptionParams) {
        HttpServerException httpServerException = new HttpServerException();
        httpServerException.setMessage(throwable.getMessage());
        httpServerException.setTimestamp(new Date().getTime());
        httpServerException.setErrorParam(exceptionParams);
        httpServerException.setErrorCode(throwable.getClass().getSimpleName());
        httpServerException.setErrorType(throwable.getClass().getSuperclass().getSimpleName());
        return httpServerException;
    }
}