package CustomizeSort

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import spark.test.GroupIP.MyUtils

object CustomSort2 {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("Iplocation2").setMaster("local[4]")
    val sc = new SparkContext(conf)
    //  val rules: Array[(Long, Long, String)] = MyUtils.readRules("/Users/lionyan/Desktop/SparkDemo/ip.txt")

    val users = Array("aaaa 29 9999", "bbb 29 998", "ccc 30 99", "dddd 31 998")

    val lines: RDD[String] = sc.parallelize(users)

    val tpRDD: RDD[(String, Int, Int)] = lines.map(line => {
      val fields = line.split(" ")
      val name = fields(0)
      val age = fields(1).toInt
      val faceValue = fields(2).toInt
      (name, age, faceValue)
    })
    val sorted: RDD[(String, Int, Int)] = tpRDD.sortBy(tp => new Boy(tp._2, tp._3))

    //     val r: Array[User] = sorted.collect()

    println(sorted.collect().toBuffer)

    sc.stop()
  }
}

class Boy(val age: Int, val faceValue: Int) extends Ordered[Boy] with Serializable {
  override def compare(that: Boy): Int = {
    /*
    * faceValue:正序
    * age：倒序
    * */
    if (this.faceValue == that.faceValue) {
      this.age - that.age
    } else {
      -(this.faceValue - that.faceValue)
    }

  }

}