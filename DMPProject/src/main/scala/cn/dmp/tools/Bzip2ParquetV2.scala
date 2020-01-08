package cn.dmp.tools

import cn.dmp.beans.Log
import cn.dmp.utils.{NBF, SchemaUtils}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

/*
* 使用自定义类的方式构建Schema
* */
object Bzip2ParquetV2 {

  def main(args: Array[String]): Unit = {

    if (args.length != 3) {

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
    val Array(logInputPath, compressionCode, resultOutputPath) = args

    val sparkConf = new SparkConf()
    sparkConf.setAppName(s"${this.getClass.getSimpleName}")
    sparkConf.setMaster("local[*]")
    //RDD序列化到磁盘 worker与worker之间的数据传输
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    //注册自定义类的序列化方式
    sparkConf.registerKryoClasses(Array(classOf[Log]))
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)
    sqlContext.setConf("spark.sql.parquet.compression.codec", compressionCode)

    val dataLog: RDD[Log] = sc.textFile(logInputPath)
      .map(line => line.split("\\|", -1))
      .filter(_.length >= 14)
      .map(arr => Log(arr))
    val dataFrame = sqlContext.createDataFrame(dataLog)

    val configuration = sc.hadoopConfiguration
    val fileSystem = FileSystem.get(configuration)
    val resultPath = new Path(resultOutputPath)
    if (fileSystem.exists(resultPath)) {
      fileSystem.delete(resultPath, true)
    }


    dataFrame.write.parquet(resultOutputPath)

    //    定义根据什么分区
//    dataFrame.write.partitionBy("provincename").parquet(resultOutputPath)

    sc.stop()

  }

}
