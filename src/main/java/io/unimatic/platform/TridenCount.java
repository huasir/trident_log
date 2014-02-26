package io.unimatic.platform;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.metric.LoggingMetricsConsumer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.testing.FixedBatchSpout;
import storm.trident.testing.MemoryMapState;

public class TridenCount {
    public static final String TOPOLOGY_NAME = "trident_count";

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
        FixedBatchSpout spout = new FixedBatchSpout(new Fields("sentence"), 3,
                new Values("the cow jumped over the moon"),
                new Values("the man went to the store and bought some candy"),
                new Values("four score and seven years ago"),
                new Values("how many apples can you eat"));
        spout.setCycle(true);

        TridentTopology topology = new TridentTopology();
        TridentState wordCounts =
                topology.newStream("spout1", spout)
                        .each(new Fields("sentence"), new MySplit(), new Fields("word"))
                        .persistentAggregate(new MemoryMapState.Factory(), new MyCount(), new Fields("count"))
                        .parallelismHint(1);

        Config conf = new Config();
        conf.setDebug(true);
        conf.registerMetricsConsumer(LoggingMetricsConsumer.class, 2);

//        LocalCluster cluster = new LocalCluster();
//        cluster.submitTopology(TOPOLOGY_NAME, conf, topology.build());

        conf.setNumWorkers(2);
        StormSubmitter.submitTopology(TOPOLOGY_NAME, conf, topology.build());
    }
}
