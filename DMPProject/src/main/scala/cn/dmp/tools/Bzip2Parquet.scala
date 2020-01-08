package cn.dmp.tools

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}
import cn.dmp.utils.{NBF, SchemaUtils}


object Bzip2Parquet {

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
    val Array(logInputPath, compressionCode, resultOutputPath , numPartition) = args

    val sparkConf = new SparkConf()
    sparkConf.setAppName(s"${this.getClass.getSimpleName}")
    sparkConf.setMaster("local[*]")
    //RDD序列化到磁盘 worker与worker之间的数据传输
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val sc = new SparkContext(sparkConf)


    val sqlContext = new SQLContext(sc)
    sqlContext.setConf("spark.sql.parquet.compression.codec", compressionCode)
    /*
    * numPartition  添加分区数达到增加executor的效果
    * */
    val rawData = sc.textFile(logInputPath).repartition(numPartition.toInt)

    //    一直切分到最后一位的两种写法
    //    rawData.map(_.split(",",-1))
    val dataRow: RDD[Row] = rawData
      .map(line => line.split("|", line.length))
      .filter(_.length >= 14)
      .map(arr => {
        Row(arr(0),
//          NBF.toInt(arr(1))
          arr(6)
        )
      })
    val dataFrame = sqlContext.createDataFrame(dataRow, SchemaUtils.logStructType)
    dataFrame.write.parquet(resultOutputPath)
    sc.stop()

  }

}
