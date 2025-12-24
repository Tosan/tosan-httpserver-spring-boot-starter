package com.tosan.http.server.starter.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;

import java.util.Arrays;

public class MetricFilter implements MeterFilter {

    private MeterFilterConfig meterFilterConfig;

    public MetricFilter(MeterFilterConfig meterFilterConfig) {
        this.meterFilterConfig = meterFilterConfig;
    }

    @Override
    public MeterFilterReply accept(Meter.Id id) {

        if (meterFilterConfig.getInclude() != null) {
            for (FilterRule rule : meterFilterConfig.getInclude()) {

                if (!id.getName().equals(rule.getName())) continue;

                boolean tagsMatch = rule.getTags().entrySet().stream()
                        .allMatch(e -> id.getTags().contains(Tag.of(e.getKey(), e.getValue())));

                if (tagsMatch) {
                    return MeterFilterReply.ACCEPT;
                }
            }
        }

        if (meterFilterConfig.getExcludedMeterNames() != null && Arrays.asList(meterFilterConfig.getExcludedMeterNames()).contains(id.getName())) {
            return MeterFilterReply.DENY;
        }
        if (meterFilterConfig.getExcludedMeterTags() != null && !meterFilterConfig.getExcludedMeterTags().isEmpty()) {
            for (String key : meterFilterConfig.getExcludedMeterTags().keySet()) {
                if (id.getTags().contains(Tag.of(key, meterFilterConfig.getExcludedMeterTags().get(key)))) {
                    return MeterFilterReply.DENY;
                }
            }
        }
        return MeterFilterReply.NEUTRAL;
    }
}