package com.nathan.learn.spark.basic

import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]) {
    val inputFile = "file:///E:\\github\\bigdata\\bigdata\\spark\\build.gradle"
    val conf = new SparkConf().setAppName("WordCount").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val textFile = sc.textFile(inputFile)
    val wordCount = textFile.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey(_+_)
//    wordCount.foreach(println)
    import org.json4s._
    import org.json4s.jackson.Serialization.write
    implicit val formats = DefaultFormats//数据格式化时需要
    val str = write(wordCount.collect)
    print(str)
  }
}