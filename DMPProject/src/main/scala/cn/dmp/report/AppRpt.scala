package cn.dmp.report

import cn.dmp.beans.Log
import cn.dmp.utils.RptUtils
import org.apache.commons.lang.StringUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.{SparkConf, SparkContext}

object AppRpt extends App {

  if (args.length != 3) {

    println("参数有误")

    sys.exit()
  }

  /*
  * logInputPath:入参地址
  * dicFilePath:字典文件路径
  * resultOutputPath:出参地址
  *
  * 示例参数：
  * /Users/lionyan/Desktop/SparkDemo/ip.txt
  * snappy
  * /Users/lionyan/Desktop/SparkDemo/snappy
  *
  *
  * */
  val Array(logInputPath, dicFilePath, resultOutputPath) = args

  val sparkConf = new SparkConf()
  sparkConf.setAppName(s"${this.getClass.getSimpleName}")
  sparkConf.setMaster("local[*]")
  //RDD序列化到磁盘 worker与worker之间的数据传输
  sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  val sc = new SparkContext(sparkConf)

  //字典文件  如果不collect,读取的是分片后的数据,collect之后是完整数据
  private val dictMap: Map[String, String] = sc.textFile(dicFilePath).map(line => {
    val files = line.split("\t", -1)
    (files(4), files(1))
  }).collect().toMap

  //  字典数据广播
  val broadcast: Broadcast[Map[String, String]] = sc.broadcast(dictMap)


  sc.textFile(logInputPath)
    .map(_.split(","))
    .filter(_.length >= 85)
    .map(Log(_)).filter(log => !log.appid.isEmpty || !log.appname.isEmpty)
    .map(log => {
      var newAppName = log.appname
      if (!StringUtils.isNotEmpty(newAppName)) {
        broadcast.value.getOrElse(log.appid, "未知")
      }

      val req = RptUtils.caculateReq(log.requestmode, log.processnode)
      val rtb = RptUtils.caculateRtb(log.iseffective, log.isbilling, log.isbid, log.adorderid, log.iswin, log.winprice, log
        .adpayment)
      val showClick = RptUtils.caculateShowClick(log.requestmode, log.iseffective)

      (newAppName, req ++ rtb ++ showClick)
    }).reduceByKey((list1, list2) => {
    list1.zip(list2).map(t => t._1 + t._2)
  }).map(t => t._1 + "," + t._2.mkString(","))
    .saveAsTextFile(resultOutputPath)

  sc.stop()
}
