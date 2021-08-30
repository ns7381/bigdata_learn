package org.apache.spark.rpc.ep

import org.apache.spark.{SecurityManager, SparkConf}
import org.apache.spark.rpc.{RpcCallContext, RpcEndpoint, RpcEnv}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success


class HelloEndpoint(override val rpcEnv: RpcEnv) extends RpcEndpoint {
  override def onStart(): Unit = {
    println("start hello endpoint")
  }

  override def receiveAndReply(context: RpcCallContext): PartialFunction[Any, Unit] = {
    case SayHello(msg) => {
      println(s"receive $msg")
      context.reply(s"hello, $msg")
    }
  }

  override def receive: PartialFunction[Any, Unit] = {
    case SayHello(msg) => {
      println(s"receive $msg")
//      context.reply(s"hello, $msg")
    }
  }

  override def onStop(): Unit = {
    println("stop hello endpoint")
  }
}

case class SayHello(msg: String)

object HelloEndpoint {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    val manager = new SecurityManager(conf)
    // 创建server模式的RpcEnv
    val rpcEnv: RpcEnv = RpcEnv.create("hello-server", "localhost", 5432, conf, manager)
    // 实例化HelloEndpoint
    val helloEndpoint: RpcEndpoint = new HelloEndpoint(rpcEnv)
    // 在RpcEnv注册helloEndpoint
    val rpcEndpointRef = rpcEnv.setupEndpoint("hello-service", helloEndpoint)

    val fut = Future {
      Thread.sleep(3000)
      rpcEndpointRef.send(SayHello("test111"))
      "success"
    }

    fut onComplete {
      case Success(r) => println(s"the result is ${r}")
      case _ => println("some Exception")
    }

    // 等待线程rpcEnv运行完
    rpcEnv.awaitTermination()
  }
}