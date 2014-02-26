package io.unimatic.platform;

import backtype.storm.metric.api.CountMetric;
import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.util.Map;

public class MySplit extends BaseFunction {

    transient LogMetric logMetric;

    @Override
    public void prepare(Map conf, TridentOperationContext context) {
        logMetric = new LogMetric(MySplit.class);
        context.registerMetric("split", logMetric, 10);
    }

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        for (String word : tuple.getString(0).split(" ")) {
            if (word.length() > 0) {
                collector.emit(new Values(word));
            }
            logMetric.debug("word length is {}",word.length());
        }
    }

}