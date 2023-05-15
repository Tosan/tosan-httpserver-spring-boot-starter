package com.tosan.http.server.sample.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

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
    private Date date;
    private LocalDate localDate;
    private String mobileNumber;
    private long testNumber;
    private BigDecimal amount;
    private short age;
    private double average;
    private int length;
}