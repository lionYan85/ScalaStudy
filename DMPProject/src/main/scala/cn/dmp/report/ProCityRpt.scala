package cn.dmp.report


import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

object ProCityRpt {
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
    val sqlc = new SQLContext(sc)
    val df: DataFrame = sqlc.read.parquet(logInputPath)
    //    将dataframe注册为一张临时表
    df.registerTempTable("log")
    //    统计分组后的各省市日志记录条数
    val result: DataFrame = sqlc.sql("select provincename,count(*) from log group by provincename")

    //    result.write.json(resultOutputPath)

    //判断文件是否存在

    val configuration = sc.hadoopConfiguration
    val fileSystem = FileSystem.get(configuration)
    val resultPath = new Path(resultOutputPath)
    if (fileSystem.exists(resultPath)) {
      fileSystem.delete(resultPath, true)
    }


    result.coalesce(1).write.json(resultOutputPath)
  }


}
