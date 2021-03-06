package cn.dmp.report

import java.util.Properties

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}
import org.apache.spark.{SparkConf, SparkContext}

object ProCityRptV2 {
  def main(args: Array[String]): Unit = {


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
//    val Array(logInputPath) = args

    val sparkConf = new SparkConf()
    sparkConf.setAppName(s"${this.getClass.getSimpleName}")
    sparkConf.setMaster("local[*]")
    //RDD序列化到磁盘 worker与worker之间的数据传输
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val sc = new SparkContext(sparkConf)
    val sqlc = new SQLContext(sc)
    val df: DataFrame = sqlc.read.parquet("/Users/lionyan/Desktop/SparkDemo/snappyV2")
    //    将dataframe注册为一张临时表
    df.registerTempTable("log")
    //    统计分组后的各省市日志记录条数
    val result: DataFrame = sqlc.sql("select provincename,count(*) from log group by provincename")

    //加载配置文件 conf -> json -> properties
    val load = ConfigFactory.load()
    val properties = new Properties()
    properties.setProperty("user",load.getString("jdbc.user"))
    properties.setProperty("password",load.getString("jdbc.password"))
//    properties.setProperty("driver", "com.mysql.jdbc.Driver")
    result.write.mode(SaveMode.Overwrite).jdbc(load.getString("jdbc.url"),load.getString("jdbc.tableName"),properties)

    sc.stop()
  }


}
