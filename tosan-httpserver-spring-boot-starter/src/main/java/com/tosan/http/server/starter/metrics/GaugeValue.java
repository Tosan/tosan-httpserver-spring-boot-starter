package com.tosan.http.server.starter.metrics;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Shahryar Safizadeh
 * @since 2/13/2024
 */
public class GaugeValue {
    private static final AtomicInteger value = new AtomicInteger(0);

    public void increment() {
        value.incrementAndGet();
    }

    public void decrement() {
        value.decrementAndGet();
    }

    public AtomicInteger getValue() {
        return value;
    }
}