package CustomizeSort

import java.sql.{Connection, DriverManager}

import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.{SparkConf, SparkContext}

object JDBCDemo {

  val getConn = () => {
    DriverManager.getConnection("jdbc:mysql://172.16.12.10:3306/bigdata?characterEncoding=UTF-8", "root", "Abc@1234")
  }

  def main(args: Array[String]): Unit = {

    val conn = new SparkConf().setAppName("JDBCDemo").setMaster("local[2]")
    val sc = new SparkContext(conn)

    val jdbcRDD: JdbcRDD[(Int, String, Int)] = new JdbcRDD(
      sc,
      getConn,
      "select * from logs where id >= ? and id <= ?",
      1,
      5,
      2,
      resultSet => {
        val id = resultSet.getInt(1)
        val name = resultSet.getString(2)
        val age = resultSet.getInt(3)
        (id, name, age)
      }
    )

    val r = jdbcRDD.collect()

    println(r.toBuffer)
  }


}
