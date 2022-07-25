package com.tosan.http.server.starter.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mina khoshnevisan
 * @since 7/25/2022
 */
public class ServiceLogUtil {

    public LogDto createLogDto(ProceedingJoinPoint pjp) {
        LogDto logDto = new LogDto();
        Class joinPointLocation = pjp.getSourceLocation().getWithinType();
        String position = pjp.getSignature().getName() + " ";
        Logger logger = LoggerFactory.getLogger(joinPointLocation);
        logDto.setLogger(logger);
        logDto.setPjp(pjp);
        logDto.setPosition(position);
        return logDto;
    }

    public void logException(LogDto logDto, Throwable ex) {
        logDto.getLogger().error(logDto.getPosition() + "Response Exception: " + ex.toString() + "\n", ex);
    }

    public class LogDto {
        private ProceedingJoinPoint pjp;
        private String position;
        private Logger logger;

        public ProceedingJoinPoint getPjp() {
            return pjp;
        }

        public void setPjp(ProceedingJoinPoint pjp) {
            this.pjp = pjp;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public Logger getLogger() {
            return logger;
        }

        public void setLogger(Logger logger) {
            this.logger = logger;
        }
    }
}