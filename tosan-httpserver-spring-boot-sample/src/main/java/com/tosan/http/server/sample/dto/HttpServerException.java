package com.tosan.http.server.sample.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author M.khoshnevisan
 * @since 5/1/2021
 */
@Data
public class HttpServerException {

    private long timestamp;
    private String errorType;
    private String errorCode;
    private String message;
    private Map<String, Object> errorParam;
}