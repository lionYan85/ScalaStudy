package Steaming

import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object SteamingWordCount {

  def main(args: Array[String]): Unit = {

    val  conf = new SparkConf().setAppName("SteamingWordCount").setMaster("local[2]")

    val sc = new SparkContext(conf)

    //5秒生成一个批次
    val ssc = new StreamingContext(sc,Milliseconds(5000))

    //有了streamingContext，就可以创建sparkstreaming的抽象DStream
    val lines: ReceiverInputDStream[String] = ssc.socketTextStream("192.168.10.232",8888)

    val words: DStream[String] = lines.flatMap(_.split(" "))

    val wordAndOne: DStream[(String, Int)] = words.map((_,1))

    val reduced: DStream[(String, Int)] = wordAndOne.reduceByKey(_+_)

    reduced.print()

    //启动sparkstreaming程序
    ssc.start()

    //优雅的退出
    ssc.awaitTermination()
  }

}
