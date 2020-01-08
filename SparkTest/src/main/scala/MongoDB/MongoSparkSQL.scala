package MongoDB


import com.mongodb.spark.MongoSpark
import org.apache.spark.sql.{DataFrame, SparkSession}

object MongoSparkSQL {

  def main(args: Array[String]): Unit = {

    val session = SparkSession.builder()
      .master("local[*]")
      .appName("MongoSpark")
      .config("spark.mongodb.input.uri",
        "mongodb://172.16.12.10:27200,172.16.12.11:27200,172.16.12.12:27200/mobike" +
          ".bikes?readPreference=secondaryPreferred")
      .config("spark.mongodb.output.uri",
        "mongodb://172.16.12.10:27200,172.16.12.11:27200,172.16.12.12:27200/mobike" +
          ".bikes")
      .getOrCreate()

    val df: DataFrame = MongoSpark.load(session)

    df.createTempView("v_bikes")

    val result: DataFrame = session.sql("select * from v_bikes")

    result.show()

    session.stop()
  }

}
