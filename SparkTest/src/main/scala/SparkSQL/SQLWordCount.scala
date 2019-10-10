package SparkSQL

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.{SparkConf, SparkContext}

object SQLWordCount {

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
    //注册视图
    words.createTempView("v_wc")

    val result: DataFrame = spark.sql("select value,count(*)counts from v_wc group by value")

    result.show()

    spark.stop()

  }
}
