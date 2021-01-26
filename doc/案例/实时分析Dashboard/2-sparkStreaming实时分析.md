##### 执行sparkStreaming程序

```scala
package streaming

import java.util.UUID
import org.apache.spark.sql.SparkSession

object StructuredKafkaWordCount {
  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      System.err.println("Usage: StructuredKafkaWordCount <bootstrap-servers> " +
        "<subscribe-type> <topics> [<checkpoint-location>]")
      System.exit(1)
    }

    val Array(bootstrapServers, subscribeType, topics, _*) = args
    val checkpointLocation =
      if (args.length > 3) args(3) else "/tmp/temporary-" + UUID.randomUUID.toString

    val spark = SparkSession
      .builder
        .master("local[*]")
      .appName("StructuredKafkaWordCount")
      .getOrCreate()

    import spark.implicits._

    // Create DataSet representing the stream of input lines from kafka
    val lines = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", bootstrapServers)
      .option(subscribeType, topics)
      .load()
      .selectExpr("CAST(value AS STRING)")
      .as[String]

    // Generate running word count
    val wordCounts = lines.flatMap(_.split(" ")).groupBy("value").count()
    // Start running the query that prints the running counts to the console
    val query = wordCounts.toJSON
      .writeStream
      .outputMode("complete")
      .format("kafka")
      .option("kafka.bootstrap.servers", bootstrapServers)
      .option("topic", "result")
      .option("checkpointLocation", checkpointLocation)
      .start()

    query.awaitTermination()
  }

}
// scalastyle:on println

```

执行命令如下：

```shell
spark-submit --class "streaming.StructuredKafkaWordCount" --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.3.1 /opt/spark-1.0-SNAPSHOT.jar hdinsight-20190409175205-62-master-3.novalocal:6667 subscribe sex
```

