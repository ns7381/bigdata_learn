package com.nathan.learn.spark.day03

import com.alibaba.fastjson.{JSON, JSONException}
import com.nathan.learn.spark.CaseOrderBean
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.{Logger, LoggerFactory}

object OrderDetailToHbase {
  private val logger: Logger = LoggerFactory.getLogger(OrderDetailToHbase.getClass)

  def main(args: Array[String]): Unit = {
    val isLocal = args(0).toBoolean
    val conf = new SparkConf().setAppName("test")
    if (isLocal) {
      conf.setMaster("local[*]")
    }
    val sc = new SparkContext(conf)

    val lines = sc.textFile(args(1))
    val beanRDD = lines.map(line => {
      var bean: CaseOrderBean = null
      try {
        bean = JSON.parseObject(line, classOf[CaseOrderBean])
      } catch {
        case _: JSONException =>
          logger.error("Parse json error, " + line)
      }
      bean
    })
    val filterRDD: RDD[CaseOrderBean] = beanRDD.filter(_ != null)
  }
}
