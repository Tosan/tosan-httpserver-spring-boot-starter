package com.tosan.http.server.starter.metrics.util;

import com.tosan.http.server.starter.metrics.GaugeValue;
import com.tosan.http.server.starter.metrics.enumuration.MeterType;
import io.micrometer.core.instrument.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Shahryar Safizadeh
 * @since 2/13/2024
 */
public class MeterUtil {

    private MeterRegistry meterRegistry;
    private final Map<String, Meter> meters = new HashMap<>();
    private final Map<String, GaugeValue> gaugeMeters = new HashMap<>();

    @Autowired
    public void setMeterRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Meter registerMeter(MeterType meterType, String meterName, String description, Tags tags) {
        String key = createKey(meterName, tags);
        if (!meters.containsKey(key)) {
            synchronized (meters) {
                meters.computeIfAbsent(key, ignore -> getMeter(meterType, meterName, description, tags));
            }
        }
        return meters.get(key);
    }

    public void updateTimerMeter(String metricName, Tags tags, long duration) {
        String key = createKey(metricName, tags);
        if (!meters.isEmpty() && meters.get(key) != null) {
            Meter meter = meters.get(key);
            ((Timer) meter).record(duration, TimeUnit.MILLISECONDS);
        }
    }

    public void updateCounterMeter(String metricName, Tags tags) {
        String key = createKey(metricName, tags);
        if (!meters.isEmpty() && meters.get(key) != null) {
            Meter meter = meters.get(key);
            ((Counter) meter).increment();
        }
    }

    public void updateGaugeMeterByIncrementing(String metricName, Tags tags) {
        String key = createKey(metricName, tags);
        if (!meters.isEmpty() && gaugeMeters.get(key) != null) {
            GaugeValue gaugeValue = gaugeMeters.get(createKey(metricName, tags));
            gaugeValue.increment();
            gaugeMeters.put(createKey(metricName, tags), gaugeValue);
        }
    }

    public void updateGaugeMeterByDecrementing(String metricName, Tags tags) {
        String key = createKey(metricName, tags);
        if (!gaugeMeters.isEmpty() && gaugeMeters.get(key) != null) {
            GaugeValue gaugeValue = gaugeMeters.get(createKey(metricName, tags));
            gaugeValue.decrement();
            gaugeMeters.put(createKey(metricName, tags), gaugeValue);
        }
    }

    private Meter getMeter(MeterType meterType, String meterName, String description, Tags tags) {
        switch (meterType) {
            case COUNTER: {
                return registerCounterMeter(meterName, description, tags);
            }
            case TIMER: {
                return registerTimerMeter(meterName, description, tags);
            }
            case GAUGE: {
                return registerGaugeMeter(meterName, description, tags);
            }
            default: {
                return null;
            }
        }
    }

    private String createKey(String meterName, Tags tags) {
        if (meterName == null) {
            throw new RuntimeException("meter name is null");
        }
        if (tags != null) {
            return String.join("_", meterName, tags.stream().map(Tag::getValue).collect(Collectors.joining("_")));
        } else {
            return meterName;
        }
    }

    private Counter registerCounterMeter(String metricName, String description, Tags tags) {
        if (tags == null) {
            return Counter.builder(metricName)
                    .description(description)
                    .register(meterRegistry);
        } else {
            return Counter.builder(metricName)
                    .description(description)
                    .tags(tags)
                    .register(meterRegistry);
        }
    }

    private Timer registerTimerMeter(String metricName, String description, Tags tags) {
        if (tags == null) {
            return Timer.builder(metricName)
                    .description(description)
                    .publishPercentiles(0.5, 0.95)
                    .register(meterRegistry);
        } else {
            return Timer.builder(metricName)
                    .description(description)
                    .tags(tags)
                    .publishPercentiles(0.5, 0.95)
                    .register(meterRegistry);
        }
    }

    private Gauge registerGaugeMeter(String metricName, String desctiption, Tags tags) {
        GaugeValue gaugeValue = new GaugeValue();
        Gauge gauge;
        if (tags == null) {
            gauge = Gauge.builder(metricName, gaugeValue::getValue)
                    .description(desctiption)
                    .register(meterRegistry);
        } else {
            gauge = Gauge.builder(metricName, gaugeValue::getValue)
                    .description(desctiption)
                    .tags(tags)
                    .register(meterRegistry);
        }
        gaugeMeters.put(createKey(metricName, tags), gaugeValue);
        return gauge;
    }
}
