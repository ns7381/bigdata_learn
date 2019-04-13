object datasource {

  def main(args: Array[String]): Unit = {
    import org.apache.spark.sql.SparkSession

    val spark = SparkSession
      .builder()
      .master("local")
      .enableHiveSupport()
      .appName("Spark SQL basic example")
      //      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    val peopleDF = spark.read.format("json").load("spark/src/main/resources/people.json")
    peopleDF.select("name", "age").write.format("parquet").save("namesAndAges.parquet")

    val peopleDFCsv = spark.read.format("csv")
      .option("sep", ";")
      .option("inferSchema", "true")
      .option("header", "true")
      .load("spark/src/main/resources/people.csv")

    peopleDF.write.format("orc")
      .option("orc.bloom.filter.columns", "favorite_color")
      .option("orc.dictionary.key.threshold", "1.0")
      .save("users_with_options.orc")

    val sqlDF = spark.sql("SELECT * FROM parquet.`spark/src/main/resources/users.parquet`")

    case class Record(key: Int, value: String)

    import spark.sql

    sql("CREATE TABLE IF NOT EXISTS src (key INT, value STRING) USING hive")
  }
}
