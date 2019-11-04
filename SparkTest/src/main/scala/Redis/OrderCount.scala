package Redis

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Duration, StreamingContext}

import scala.collection.mutable

/**
  * Created by zx on 2017/7/31.
  */
object OrderCount {

  def main(args: Array[String]): Unit = {

    //指定组名
    val group = "g001"
    //创建SparkConf
    val conf = new SparkConf().setAppName("KafkaDirectWordCount").setMaster("local[*]")
    //创建SparkStreaming，并设置间隔时间
    val ssc = new StreamingContext(conf, Duration(2000))
    //指定消费的 topic 名字
    val topic = "kafka010"
    //指定kafka的broker地址(sparkStream的Task直连到kafka的分区上，用更加底层的API消费，效率更高)
        val brokerList = "172.16.12.10:9092,172.16.12.11:9092,172.16.12.12:9092"
//    val brokerList = "172.16.12.10:9092"

    //创建 stream 时使用的 topic 名字集合，SparkStreaming可同时消费多个topic
    val topics: Set[String] = Set(topic)


    //准备kafka的参数
    val kafkaParams = mutable.HashMap[String, String]()
    kafkaParams.put("bootstrap.servers", brokerList)
    //必须添加以下参数，否则会报错
    kafkaParams.put("group.id", "group1")
    kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")


    //    var kafkaStream: InputDStream[(String, String)] = null
    //在Kafka中记录读取偏移量
    val kafkaStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      //位置策略
      PreferConsistent,
      //订阅的策略
      Subscribe[String, String](topics, kafkaParams)
    )

    val broadcastRef = IPUtils.boradcastIpRules(ssc, "/Users/lionyan/Desktop/SparkDemo/ip.txt")

    kafkaStream.foreachRDD { kafkaRDD =>

      //判断当前的kafkaStream中的RDD是否有数据
      if (!kafkaRDD.isEmpty()) {
        //只有KafkaRDD可以强转成HasOffsetRanges，并获取到偏移量
        val offsetRanges = kafkaRDD.asInstanceOf[HasOffsetRanges].offsetRanges

        val lines: RDD[String] = kafkaRDD.map(_.value())

        //整理数据
        val fields: RDD[Array[String]] = lines.map(_.split(" "))

        //计算成交总金额
        CalculateUtil.calculateTotal(fields)

        //计算商品分类金额
        CalculateUtil.calculateItem(fields)

        //计算区域成交金额
        CalculateUtil.calculateZone(fields, broadcastRef)
        //更新偏移量
        // some time later, after outputs have completed
        kafkaStream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)

      }
    }

    ssc.start()
    ssc.awaitTermination()

  }


}
