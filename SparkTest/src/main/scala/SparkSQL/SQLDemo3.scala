package SparkSQL

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

object SQLDemo3 {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("SQLDemo3").setMaster("local[*]")
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
//    val bdf: DataFrame = rowRDD.toDF

    //不注册临时表
    val df1: DataFrame = bdf.select("name","age","fv")
    import sqlContext.implicits._
    val df2: Dataset[Row] = df1.orderBy($"fv" desc,$"age")

    //写SQL
//    val result: DataFrame = sqlContext.sql("select * from t_boy order by fv desc,age asc")
    //查看结果
    df2.show()

    //释放资源
    sc.stop()
  }
}
