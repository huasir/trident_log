package io.unimatic.platform;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Map;

public class SpringLogbackBean implements InitializingBean {

    org.slf4j.Logger _logger = LoggerFactory.getLogger(this.getClass());

    private Level logLevel = Level.INFO;
    private AppenderBase appender;

    public AppenderBase getAppender() {
        return appender;
    }

    public void setAppender(AppenderBase appender) {
        this.appender = appender;
    }

    private Object logName = "root";
    private Level filterLevel = Level.INFO;
    private boolean useFilterLevel = true;
    private boolean additiveAppender = true;
    private String pattern = "%d{HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n";

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public void setLogName(Object logName) {
        this.logName = logName;
    }

    public void setFilterLevel(Level filterLevel) {
        this.filterLevel = filterLevel;
    }

    public void setAdditiveAppender(boolean additiveAppender) {
        this.additiveAppender = additiveAppender;
    }

    public void setUseFilterLevel(boolean useFilterLevel) {
        this.useFilterLevel = useFilterLevel;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(appender, "property `appender` must set!");
        buildAppender();
        if (logName.getClass().getSimpleName().equals("LinkedHashMap")) {
            Map<String, String> loggers = (Map) logName;
            for (Map.Entry<String, String> log : loggers.entrySet()) {
                Logger logger = (Logger) LoggerFactory.getLogger(log.getKey());
                logger.setLevel(Level.toLevel(log.getValue()));
                logger.setAdditive(additiveAppender);
                logger.addAppender(appender);
            }
        }
        else {
            Logger logger = (Logger) LoggerFactory.getLogger(logName.toString());
            logger.setLevel(logLevel);
            logger.setAdditive(additiveAppender);
            logger.addAppender(appender);
        }

    }

    private void buildAppender() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        if (useFilterLevel) {
            LevelFilter levelFilter = new LevelFilter();
            levelFilter.setContext(loggerContext);
            levelFilter.setLevel(filterLevel);
            levelFilter.setOnMatch(FilterReply.ACCEPT);
            levelFilter.setOnMismatch(FilterReply.DENY);
            levelFilter.start();
            appender.addFilter(levelFilter);
        }
        appender.setContext(loggerContext);
        appender.start();
    }
}