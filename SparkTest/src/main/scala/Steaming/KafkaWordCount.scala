package Steaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scala.collection.mutable

object KafkaWordCount {

  def main(args: Array[String]): Unit = {


//    val brokerList = "172.16.12.10:9092,172.16.12.11:9092,172.16.12.12:9092"
val brokerList = "172.16.12.10:9092"
    val topic = "kafka010"
    val conf = new SparkConf().setAppName("kafkawordcount").setMaster("local[*]")

    val ssc = new StreamingContext(conf, Seconds(2))

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

    val reduced: DStream[(String, Int)] = wordsAndOne.reduceByKey(_ + _)

    reduced.print()

    ssc.start()

    ssc.awaitTermination()
  }

}
