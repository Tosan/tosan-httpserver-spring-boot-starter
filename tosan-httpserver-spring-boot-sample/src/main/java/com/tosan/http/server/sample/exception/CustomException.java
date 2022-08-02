package com.tosan.http.server.sample.exception;

/**
 * @author mina khoshnevisan
 * @since 8/2/2022
 */
public class CustomException extends RuntimeException{

    private String secretKey;

    public CustomException(String message, String secretKey) {
        super(message);
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}