package CustomizeSort

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import spark.test.GroupIP.MyUtils

object CustomSort1 {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("Iplocation1").setMaster("local[4]")
    val sc = new SparkContext(conf)
    //  val rules: Array[(Long, Long, String)] = MyUtils.readRules("/Users/lionyan/Desktop/SparkDemo/ip.txt")

    val users = Array("aaaa 29 9999", "bbb 29 998", "ccc 30 99", "dddd 31 998")

    val lines: RDD[String] = sc.parallelize(users)

     val userRDD: RDD[User] =lines.map(line => {
      val fields = line.split(" ")
      val name = fields(0)
      val age = fields(1).toInt
      val faceValue = fields(2).toInt
      new User(name, age, faceValue)
    })
     val sorted: RDD[User] = userRDD.sortBy(u => u)

     val r: Array[User] = sorted.collect()

    println(r.toBuffer)

    sc.stop()
  }
}

class User(val name: String, val age: Int, val faceValue: Int) extends
  Ordered[User] with Serializable {
  override def compare(that: User): Int = {
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

  override def toString: String = s"name:$name,age:$age,fv:$faceValue"
}