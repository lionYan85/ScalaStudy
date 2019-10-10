package SparkSQL

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.{SparkConf, SparkContext}

object DataSetWordCount {

  def main(args: Array[String]): Unit = {

    /*spark2.x SQL执行入口*/
    val spark = SparkSession.builder()
      .appName("SQLTest1")
      .master("local[*]")
      .getOrCreate()

    //dataset 分布式数据集，对RDD进一步封装，更智能的RDD
    val lines: Dataset[String] = spark.read.textFile("/Users/lionyan/Desktop/SparkDemo/SparkWordCount")
    //导入隐式转换
    import spark.implicits._
    val words: Dataset[String] = lines.flatMap(_.split(" "))

    //使用dataset的api
    val count1 = words.groupBy($"value").count().sort($"count" desc)

    count1.show()

    import org.apache.spark.sql.functions._
    val count2 = words.groupBy($"value").agg(count("*") as "counts" )
    count2.show()


    spark.stop()

  }
}
