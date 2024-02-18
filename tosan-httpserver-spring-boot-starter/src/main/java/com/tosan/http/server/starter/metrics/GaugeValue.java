package com.tosan.http.server.starter.metrics;

/**
 * @author Shahryar Safizadeh
 * @since 2/13/2024
 */
public class GaugeValue {
    private int value = 0;

    public void increment() {
        value++;
    }

    public void decrement() {
        value--;
    }

    public int getValue() {
        return value;
    }
}