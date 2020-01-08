package cn.dmp.report

import cn.dmp.beans.Log
import cn.dmp.utils.RptUtils
import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

object AreaRptV2 {

  def main(args: Array[String]): Unit = {

    if (args.length != 2) {

      println("参数有误")

      sys.exit()
    }

    /*
    * logInputPath:入参地址
    * compressionCode:压缩方式
    * resultOutputPath:出参地址
    *
    * 示例参数：
    * /Users/lionyan/Desktop/SparkDemo/ip.txt
    * snappy
    * /Users/lionyan/Desktop/SparkDemo/snappy、、
    * 、、、、
    *
    * */
    val Array(logInputPath, resultOutputPath) = args

    val sparkConf = new SparkConf()
    sparkConf.setAppName(s"${this.getClass.getSimpleName}")
    sparkConf.setMaster("local[*]")
    //RDD序列化到磁盘 worker与worker之间的数据传输
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val sc = new SparkContext(sparkConf)

//    sc.textFile(logInputPath)
//      .map(_.split("\\|",-1))
//      .filter(_.length>=14)
//      .map(arr => {
//
//        val log = Log(arr)
////        RptUtils.caculateReq(log.)
//              val reqList = RptUtils.caculateReq(log.reqMode, log.prcNode)
//              val rtbList = RptUtils.caculateRtb(log.effTive, log.bill, log.bid, log.orderId, log.win, log.winPrice, log.adPayMent)
//              val showClickList = RptUtils.caculateShowClick(log.reqMode, log.effTive)
//
//
//        ((log.provincename,log.cityname),reqList ++ rtbList ++ showClickList))
//    })


    // 读取parquet文件
//    val sQLContext = new SQLContext(sc)
//    val parquetData= sQLContext.read.parquet(logInputPath)

//    parquetData.map(row => {
//      // 是不是原始请求，有效请求，广告请求 List(原始请求，有效请求，广告请求)
//      val reqMode = row.getAs[Int]("requestmode")
//      val prcNode = row.getAs[Int]("processnode")
//      // 参与竞价, 竞价成功  List(参与竞价，竞价成功, 消费, 成本)
//      val effTive = row.getAs[Int]("iseffective")
//      val bill = row.getAs[Int]("isbilling")
//      val bid = row.getAs[Int]("isbid")
//      val orderId = row.getAs[Int]("adorderid")
//      val win = row.getAs[Int]("iswin")
//      val winPrice = row.getAs[Double]("winprice")
//      val adPayMent = row.getAs[Double]("adpayment")
//
//
//      val reqList = RptUtils.caculateReq(reqMode, prcNode)
//      val rtbList = RptUtils.caculateRtb(effTive, bill, bid, orderId, win, winPrice, adPayMent)
//      val showClickList = RptUtils.caculateShowClick(reqMode, effTive)
//
//      // 返回元组
//      ((row.getAs[String]("provincename"), row.getAs[String]("cityname")), reqList ++ rtbList ++ showClickList)
//
//    }).reduceByKey((list1, list2) => {
//      list1.zip(list2).map(t => t._1 + t._2)
//    }).map(t => t._1._1 + "," + t._1._2 + "," + t._2.mkString(","))
//      .saveAsTextFile(resultOutputPath)

    sc.stop()
  }

}
