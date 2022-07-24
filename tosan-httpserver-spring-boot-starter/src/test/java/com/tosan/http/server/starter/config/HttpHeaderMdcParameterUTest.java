package com.tosan.http.server.starter.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;

import static com.tosan.http.server.starter.TestLogUtil.getAppenderList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author mina khoshnevisan
 * @since 7/19/2022
 */
public class HttpHeaderMdcParameterUTest {

    @Test
    public void testHttpHeaderMdcParameterBuilder_setInvalidParameterName_warnInvalidInput() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder.class);
        new HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder(null, "test").build();
        assertEquals("invalid header parameter name:null", listAppender.list.get(0).getFormattedMessage());
    }

    @Test
    public void testHttpHeaderMdcParameterBuilder_setInvalidMdcParameterName_warnInvalidInput() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder.class);
        new HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder("test", null).build();
        assertEquals("invalid header parameter name:null", listAppender.list.get(0).getFormattedMessage());
    }

    @Test
    public void testHttpHeaderMdcParameterBuilder_setInvalidRandomParameter_warnInvalidInput() {
        ListAppender<ILoggingEvent> listAppender = getAppenderList(HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder.class);
        new HttpHeaderMdcParameter.HttpHeaderMdcParameterBuilder("test", "test")
                .randomParameter(new RandomParameter(null, 8))
                .build();
        assertEquals("invalid mdc random generation type:null", listAppender.list.get(0).getFormattedMessage());
    }
}