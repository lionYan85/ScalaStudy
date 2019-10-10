package SparkSQL

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, Row, SQLContext}

object SQLDemo2 {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("SQLDemo2").setMaster("local[*]")
    //创建sparksql的链接
    val sc = new SparkContext(conf)
    //包装sparkcontext
    val sqlContext = new SQLContext(sc)

    val lines = sc.textFile("/Users/lionyan/Desktop/SparkDemo/sqltest")

    val rowRDD: RDD[Row] = lines.map(line => {
      val fields = line.split(",")
      val id = fields(0).toLong
      val name = fields(1)
      val age = fields(2).toInt
      val fv = fields(3).toDouble

      Row(id, name, age, fv)
    })

    //结构类型,就是表头,用于描述dataframe
    val sch:StructType = StructType(List(
      StructField("id",LongType,true),
      StructField("name",StringType,true),
      StructField("age",IntegerType,true),
      StructField("fv",DoubleType,true)
    ))

    val bdf = sqlContext.createDataFrame(rowRDD,sch)

    //将RDD转换为dataframe
    import sqlContext.implicits._
//    val bdf: DataFrame = rowRDD.toDF

    //注册为临时表
    bdf.registerTempTable("t_boy")
    //写SQL
    val result: DataFrame = sqlContext.sql("select * from t_boy order by fv desc,age asc")
    //查看结果
    result.show()

    //释放资源
    sc.stop()
  }
}
