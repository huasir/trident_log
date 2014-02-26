package io.unimatic.platform;

import backtype.storm.tuple.Values;
import org.slf4j.Logger;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class MySplit extends BaseFunction {

    private static final Logger logger = LoggerFactory.getLogger(MySplit.class,
            TridenCount.TOPOLOGY_NAME);

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        for (String word : tuple.getString(0).split(" ")) {
            if (word.length() > 0) {
                collector.emit(new Values(word));
            }
            logger.debug("word length is {}", word.length());
        }
    }

}