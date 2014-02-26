package io.unimatic.platform;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class StormAppender extends AppenderBase<ILoggingEvent> {
    private static Map<String, LogMetric> loggerMetricFactory = new HashMap<String, LogMetric>();

    private String topic;
    private PatternLayout patternLayout;
    private String pattern;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public PatternLayout getPatternLayout() {
        return patternLayout;
    }

    public void setPatternLayout(PatternLayout patternLayout) {
        this.patternLayout = patternLayout;
    }

    @Override
    public void start() {
        Assert.notNull(topic, "topic not null !");
        patternLayout = new PatternLayout();
        patternLayout.setPattern(pattern);
        patternLayout.setContext(context);
        patternLayout.setOutputPatternAsHeader(false);
        patternLayout.start();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }


    @Override
    public void append(ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }
        LogMetric logMetric = loggerMetricFactory.get(event.getLoggerName());
        if (logMetric == null) return;
        logMetric.addLoggerInfo(patternLayout.doLayout((ILoggingEvent) event));
    }

    public static void registerMetric(String className, LogMetric logMetric) {
        loggerMetricFactory.put(className, logMetric);
    }

}