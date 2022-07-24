package com.tosan.http.server.starter;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

/**
 * @author M.khoshnevisan
 * @since 6/29/2021
 */
public class TestLogUtil {
    public static ListAppender<ILoggingEvent> getAppenderList(Class loggingClass) {
        Logger httpLogUtilLogger = (Logger) LoggerFactory.getLogger(loggingClass);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        httpLogUtilLogger.addAppender(listAppender);
        return listAppender;
    }
}