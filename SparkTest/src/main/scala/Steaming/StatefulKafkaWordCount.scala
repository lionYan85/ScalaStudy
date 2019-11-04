package Steaming

import org.apache.spark.{HashPartitioner, SparkConf}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

object StatefulKafkaWordCount {

  /*
  * 第一个参数：Key，就是单词
  * 第二个参数：当前批次产生批次改单词再每一个分区出现的次数
  * 第三个参数：初始值或累加的中间结果
  * */

  val updateFunc = (iter: Iterator[(String, Seq[Int], Option[Int])]) => {
    //    iter.map(t=>(t._1,t._2.sum + t._3.getOrElse(0)))

    iter.map {
      case (x, y, z) => (x, y.sum + z.getOrElse(0))
    }

  }


  def main(args: Array[String]): Unit = {

    val brokerList = "172.16.12.10:9092,172.16.12.11:9092,172.16.12.12:9092"
//val brokerList = "172.16.12.10:9092"
    val topic = "kafka010"
    val conf = new SparkConf().setAppName("kafkawordcount").setMaster("local[*]")

    val ssc = new StreamingContext(conf, Seconds(5))
    ssc.checkpoint("./spark_checkpoint")
    val topicsSet: Set[String] = Set(topic)
    val kafkaParams = mutable.HashMap[String, String]()
    kafkaParams.put("bootstrap.servers", brokerList)
    //必须添加以下参数，否则会报错
    kafkaParams.put("group.id", "group1")
    kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")


    val message = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topicsSet, kafkaParams)

    )


    //对数据进行处理
    val lines: DStream[String] = message.map(_.value())

    val words: DStream[String] = lines.flatMap(_.split(" "))

    val wordsAndOne: DStream[(String, Int)] = words.map((_, 1))


    wordsAndOne.print()

    val reduced: DStream[(String, Int)] = wordsAndOne.updateStateByKey(updateFunc, new HashPartitioner(ssc
      .sparkContext.defaultParallelism), true)

    reduced.print()

    ssc.start()

    ssc.awaitTermination()
  }

}
