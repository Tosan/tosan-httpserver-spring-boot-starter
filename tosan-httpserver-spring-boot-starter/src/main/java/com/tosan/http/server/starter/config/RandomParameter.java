package com.tosan.http.server.starter.config;

/**
 * @author mina khoshnevisan
 * @since 7/16/2022
 */
public class RandomParameter {
    private String prefix = "";
    private RandomGenerationType generationType;
    private int length;

    public RandomParameter(String prefix, RandomGenerationType generationType, int length) {
        this.prefix = prefix;
        this.generationType = generationType;
        this.length = length;
    }

    public RandomParameter(RandomGenerationType generationType, int length) {
        this.generationType = generationType;
        this.length = length;
    }

    public String getPrefix() {
        return prefix;
    }

    public RandomGenerationType getGenerationType() {
        return generationType;
    }

    public int getLength() {
        return length;
    }
}