package kafka.junit;

import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import net.mguenther.kafka.junit.KeyValue;
import net.mguenther.kafka.junit.SendKeyValues;
import net.mguenther.kafka.junit.SendValues;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sparkstreaming.poc.SparkKafkaStreaming;

import java.util.Arrays;
import java.util.stream.Collectors;

import static net.mguenther.kafka.junit.EmbeddedKafkaCluster.provisionWith;
import static net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig.defaultClusterConfig;

class SparkKafkaStreamingTest {

    private EmbeddedKafkaCluster kafka;
    @BeforeEach
    void setUp() {
        kafka = provisionWith(defaultClusterConfig());
        kafka.start();
    }
    @AfterEach
    void tearDownKafka() {
        kafka.stop();
    }

    @Test
    void kafkaTest() throws Exception {
        Headers headers = new RecordHeaders();
        headers.add("test", "test".getBytes());
        KeyValue<String, String> kv = new KeyValue<>("first Key", "first value", headers);

        kafka.send(SendKeyValues.to("test-topic", Arrays.asList(kv)));
        System.out.println(kafka.getBrokerList());
//        kafka.observe(on("test-topic", 3));

        Dataset<Row> rowDataset = SparkKafkaStreaming.sparkKafkaStreaming();
        rowDataset.collectAsList().stream().map(item->((GenericRowWithSchema)item)).collect(Collectors.toList());
    }

}