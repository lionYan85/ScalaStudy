package CustomizeSort

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import spark.test.GroupIP.MyUtils

object CustomSort6 {
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

    /*
    * 使用元组比较规则，元组先比第一个，如果第一个相等再比第二个
    * Ordering[(Int, Int)] 最终比较的规则格式
    * on[(String, Int, Int)] 未比较之前的数据格式
    * (t => (-t._3, t._2)) 怎样将规则转换成想要比较的格式
    * */
    implicit val rules = Ordering[(Int, Int)].on[(String, Int, Int)](t => (-t._3, t._2))
    val sorted: RDD[(String, Int, Int)] = tpRDD.sortBy(tp => tp)

    //     val r: Array[User] = sorted.collect()

    println(sorted.collect().toBuffer)

    sc.stop()
  }
}
