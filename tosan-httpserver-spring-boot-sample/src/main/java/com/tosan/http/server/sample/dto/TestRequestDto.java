package com.tosan.http.server.sample.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author mina khoshnevisan
 * @since 7/12/2022
 */
@Setter
@Getter
public class TestRequestDto {
    private String name;
    private String family;
    private String pan;
    private String test;
}