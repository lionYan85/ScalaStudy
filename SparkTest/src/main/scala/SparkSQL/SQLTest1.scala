package SparkSQL

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql._
import org.apache.spark.{SparkConf, SparkContext}

object SQLTest1 {

  def main(args: Array[String]): Unit = {

    /*spark2.x SQL执行入口*/
    val session = SparkSession.builder()
      .appName("SQLTest1")
      .master("local[*]")
      .getOrCreate()

    val lines: RDD[String] = session.sparkContext.textFile("/Users/lionyan/Desktop/SparkDemo/sqltest")

    val rowRDD: RDD[Row] = lines.map(line => {
      val fields = line.split(",")
      val id = fields(0).toLong
      val name = fields(1)
      val age = fields(2).toInt
      val fv = fields(3).toDouble

      Row(id, name, age, fv)
    })

    //结构类型,就是表头,用于描述dataframe
    val schema:StructType = StructType(List(
      StructField("id",LongType,true),
      StructField("name",StringType,true),
      StructField("age",IntegerType,true),
      StructField("fv",DoubleType,true)
    ))

    //创建dataframe
    val df = session.createDataFrame(rowRDD,schema)

    import session.implicits._
    val df2 = df.where($"name" > 99).orderBy($"fv" desc)

    df2.show()

//    df2.write.jdbc()

    session.stop()

  }
}
