package com.nathan.learn.spark.basic

import org.apache.spark.sql.SparkSession

object WordCountDF {
  case class Word(value: String)
  def main(args: Array[String]) {
    val inputFile = "file:///E:\\github\\bigdata\\bigdata\\spark\\build.gradle"
    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("wc")
      .getOrCreate()
    import spark.implicits._
    val textFile = spark.read.text(inputFile).as[String]
    val wordCount = textFile.flatMap(_.split(" ")).groupBy("value").count()
//    wordCount.foreach(println)
    import org.json4s._
    import org.json4s.jackson.Serialization.write
    implicit val formats = DefaultFormats//数据格式化时需要
    val str = write(wordCount.rdd.collect)
    print(str)
  }
}