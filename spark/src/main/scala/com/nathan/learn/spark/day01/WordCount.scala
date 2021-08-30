package com.nathan.learn.spark.day01

import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("wordcount").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val lines = sc.makeRDD(List("hello work", "hello world"))
    val words = lines.flatMap(line => line.split(" "))
    val pairs = words.map(word=>(word, 1))
    //写法简化:  val count = pairs.reduceByKey(_+_)
    //第一个_下划线就代表x，第二个_下划线代表y
    val count = pairs.reduceByKey((x, y) => x + y)
    count.foreach(c => println("word: " + c._1 + ", time: " + c._2))
  }
}
