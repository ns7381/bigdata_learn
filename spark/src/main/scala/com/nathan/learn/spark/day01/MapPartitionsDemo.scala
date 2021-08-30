package com.nathan.learn.spark.day01

import org.apache.spark.{SparkConf, SparkContext}

/*
 *就是将RDD的每一个分区拿出来，作为外部传入的函数
 */
object MapPartitionsDemo {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("test").setMaster("local")
    val sc = new SparkContext(conf)
    val rdd1 = sc.parallelize(List(1,2,3,4,5,6,7))
    //parallelize equal makeRDD(List(1,2,3,4,5,6,7), 3)
    val rdd2 = rdd1.map(i => i * 10)
    println("map: i*10")
    rdd2.foreach(i => print(i + " "))
    //maoPartitions方法，将RDD要计算的数据以一个分区的形式遍历出来，应用外部传入的函数
    //一个分区会对应多条数据，一个分区就是一个迭代器
    val rdd3 = rdd2.mapPartitions(it => it.map(i => i * 10))
    println("mapPartitions: i*10")
    rdd3.foreach(i => print(i + " "))
    val rdd4 = rdd3.mapPartitions(it => it.filter(i => i % 3 == 0))
    println("mapPartitions: i%3")
    rdd4.foreach(i => print(i + " "))
    sc.stop()
  }

}
