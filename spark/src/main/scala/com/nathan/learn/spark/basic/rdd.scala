package com.nathan.learn.spark.basic

import org.apache.spark.{SparkConf, SparkContext}

object rdd {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("WordCount").setMaster("local[2]")
    val sc = new SparkContext(conf)
    //创建rdd
    //1. 从文件系统种加载
    //2. 从数组、List中创建
    val lines = sc.textFile("hdfs://localhost:9000/user/hadoop/word.txt")
    val array = Array(1, 2, 3, 4, 5)
    val rdd = sc.parallelize(array)
    val list1 = List(1, 2, 3, 4, 5)
    val rdd2 = sc.parallelize(list1)

    //RDD操作
    //1. 转换：对于RDD而言，每一次转换操作都会产生不同的RDD，供给下一个“转换”使用。
    // 转换得到的RDD是惰性求值的，也就是说，整个转换过程只是记录了转换的轨迹，并不会发生真正的计算，只有遇到行动操作时，才会发生真正的计算
    //* filter(func)：筛选出满足函数func的元素，并返回一个新的数据集
    //* map(func)：将每个元素传递到函数func中，并将结果返回为一个新的数据集
    //* flatMap(func)：与map()相似，但每个输入元素都可以映射到0或多个输出结果
    //* groupByKey()：应用于(K,V)键值对的数据集时，返回一个新的(K, Iterable)形式的数据集
    //* reduceByKey(func)：应用于(K,V)键值对的数据集时，返回一个新的(K, V)形式的数据集，其中的每个值是将每个key传递到函数func中进行聚合
    val list = List("Hadoop", "Spark", "Hive", "Spark")
    val rdd_opt = sc.parallelize(list)
    val pairRDD = rdd_opt.map(word => (word, 1))
    pairRDD.foreach(println)
    pairRDD.reduceByKey((a, b) => a + b).foreach(println)
    pairRDD.groupByKey().foreach(println)
    pairRDD.keys.foreach(println)
    pairRDD.sortByKey().foreach(println)
    pairRDD.mapValues(x => x + 1).foreach(println)
    val pairRDD1 = sc.parallelize(Array(("spark", 1), ("spark", 2), ("hadoop", 3), ("hadoop", 5)))
    val pairRDD2 = sc.parallelize(Array(("spark", "fast")))
    pairRDD1.join(pairRDD2).foreach(println)

    //2.行动：
    //* count() 返回数据集中的元素个数
    //* collect() 以数组的形式返回数据集中的所有元素
    //* first() 返回数据集中的第一个元素
    //* take(n) 以数组的形式返回数据集中的前n个元素
    //* reduce(func) 通过函数func（输入两个参数并返回一个值）聚合数据集中的元素
    //* foreach(func) 将数据集中的每个元素传递到函数func中运行*
    val rdd_ac = sc.parallelize(Array(("spark", 2), ("hadoop", 6), ("hadoop", 4), ("spark", 6)))
    rdd_ac.mapValues(x => (x, 1)).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2)).mapValues(x => x._1 / x._2).collect()

    //3. 持久化: 在Spark中，RDD采用惰性求值的机制，每次遇到行动操作，都会从头开始执行计算。
    // 如果整个Spark程序中只有一次行动操作，这当然不会有什么问题。
    // 但是，在一些情形下，我们需要多次调用不同的行动操作，这就意味着，每次调用行动操作，都会触发一次从头开始的计算。
    // 这对于迭代计算而言，代价是很大的，迭代计算经常需要多次重复使用同一组数据。
    // 可以通过持久化（缓存）机制避免这种重复计算的开销。
    val l = List("Hadoop", "Spark", "Hive")
    val rdd3 = sc.parallelize(l)
    rdd3.cache()
    println(rdd.count())
    println(rdd3.collect().mkString(","))

    //4. 分区：RDD是弹性分布式数据集，通常RDD很大，会被分成很多个分区，分别保存在不同的节点上。
    // RDD分区的一个分区原则是使得分区的个数尽量等于集群中的CPU核心（core）数目。
    // 可以通过设置spark.default.parallelism这个参数的值，来配置默认的分区数目

    //5. 打印元素：一般会采用语句rdd.foreach(println)或者rdd.map(println)。
    // 当采用集群模式执行时，在worker节点上执行打印语句是输出到worker节点的stdout中，而不是输出到任务控制节点Driver Program中
    // 为了能够把所有worker节点上的打印输出信息也显示到Driver Program中，可以使用collect()方法
    rdd.collect().foreach(println)
    rdd.take(100).foreach(println)

    //6. 广播变量  https://www.jianshu.com/p/295a3b3c7fdb
    // Driver每次分发任务的时候会把task和计算逻辑的变量发送给Executor，不是使用广播变量会有多份的变量副本。
    // 这样会导致消耗大量的内存导致严重的后果。
    val data = List(1, 2, 3, 4, 5, 6)
    val bdata = sc.broadcast(data)
    val rdd6 = sc.parallelize(1 to 6, 2)
    val observedSizes = rdd6.map(_ => bdata.value.size)
  }
}
