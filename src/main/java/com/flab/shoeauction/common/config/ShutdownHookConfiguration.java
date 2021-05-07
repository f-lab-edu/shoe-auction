package com.flab.shoeauction.common.config;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

public class ShutdownHookConfiguration {

    public void destroy() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.stop();

    }
}
