package cn.dmp.report

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}


/*
* 广告投放地域分布统计
* */

object AreaRpt {

  def main(args: Array[String]): Unit = {

    if (args.length != 1) {

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
    * /Users/lionyan/Desktop/SparkDemo/snappy
    * */
    val Array(logInputPath) = args

    val sparkConf = new SparkConf()
    sparkConf.setAppName(s"${this.getClass.getSimpleName}")
    sparkConf.setMaster("local[*]")
    //RDD序列化到磁盘 worker与worker之间的数据传输
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val sc = new SparkContext(sparkConf)
    val sQLContext = new SQLContext(sc)
    val dataFrame = sQLContext.read.parquet(logInputPath)
    dataFrame.registerTempTable("log")

    val result = sQLContext.sql(
      """
        |select
        |provincename, cityname,
        |sum(case when requestmode=1 and processnode >=2 then 1 else 0 end) 有效请求,
        |sum(case when requestmode=1 and processnode =3 then 1 else 0 end) 广告请求,
        |sum(case when iseffective=1 and isbilling=1 and isbid=1 and adorderid !=0 then 1 else 0 end) 参与竞价数,
        |sum(case when iseffective=1 and isbilling=1 and iswin=1 then 1 else 0 end) 竞价成功数,
        |sum(case when requestmode=2 and iseffective=1 then 1 else 0 end) 展示数,
        |sum(case when requestmode=3 and iseffective=1 then 1 else 0 end) 点击数,
        |sum(case when iseffective=1 and isbilling=1 and iswin=1 then 1.0*adpayment/1000 else 0 end) 广告成本,
        |sum(case when iseffective=1 and isbilling=1 and iswin=1 then 1.0*winprice/1000 else 0 end) 广告消费
        |from log
        |group by provincename, cityname
        |""".stripMargin)
    result.show()

    sc.stop()

  }

}
