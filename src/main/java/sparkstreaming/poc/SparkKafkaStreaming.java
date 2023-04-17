package sparkstreaming.poc;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.Properties;

public class SparkKafkaStreaming {

    private static SparkSession sparkSession;

    public static void main(String... args) {
        sparkKafkaStreaming();

    }

    public static Dataset<Row> sparkKafkaStreaming() {
        sparkSession = SparkSession.builder()
                .appName("test_streaming")
                .master("local[*]")
                .getOrCreate();
        sparkSession.sparkContext().setLogLevel("OFF");
        Properties props = new Properties();
        props.setProperty("source", "kafka");
        props.setProperty("kafka.bootstrap.servers","localhost:9092");
        props.setProperty("subscribe", "test-topic");
        props.setProperty("startingOffsets", "earliest");

        // Read data from Kafka using Spark's streaming API
        Dataset<Row> kafkaStream = loadFromKafka(props);

        // Print the data to the console
        Dataset<Row> ds = transformDataset(kafkaStream);
//        ds.writeStream()
//                .outputMode("append")
//                .format("console")
//                .start()
//                .awaitTermination();
        return ds;
    }

    public static Dataset<Row> loadFromKafka(Properties props) {
        return sparkSession.readStream()
                .format(props.getProperty("source"))
                .option("kafka.bootstrap.servers", props.getProperty("kafka.bootstrap.servers"))
                .option("subscribe", props.getProperty("subscribe"))
                .option("startingOffsets", props.getProperty("startingOffsets"))
                .load();
    }

    public static Dataset<Row> transformDataset(Dataset<Row> kafkaStream) {
           return kafkaStream
                     .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)");

    }
}
