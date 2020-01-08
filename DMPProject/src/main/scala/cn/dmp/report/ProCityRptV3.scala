package cn.dmp.report

import cn.dmp.beans.Log
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}

object ProCityRptV3 {

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
    * /Users/lionyan/Desktop/SparkDemo/snappy
    * */
    val Array(logInputPath, resultOutputPath) = args

    val sparkConf = new SparkConf()
    sparkConf.setAppName(s"${this.getClass.getSimpleName}")
    sparkConf.setMaster("local[*]")
    //RDD序列化到磁盘 worker与worker之间的数据传输
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val sc = new SparkContext(sparkConf)
    //判断文件是否存在

    val configuration = sc.hadoopConfiguration
    val fileSystem = FileSystem.get(configuration)
    val resultPath = new Path(resultOutputPath)
    if (fileSystem.exists(resultPath)) {
      fileSystem.delete(resultPath, true)
    }

    sc.textFile(logInputPath)
      .map(line => line.split("\\|", -1).filter(_.length >= 14))
      .map(arr => (arr(0), 1))
      .reduceByKey(_ + _)
      .map(t => t._1 + t._2).saveAsTextFile(resultOutputPath)
  }

}
