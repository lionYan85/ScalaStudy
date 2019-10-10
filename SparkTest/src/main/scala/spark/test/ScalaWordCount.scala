package spark.test

import java.net.URL

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

//import scala.sys.process.processInternal.URL

object ScalaWordCount {

  def main(args: Array[String]): Unit = {
    //设置应用程序名
    val conf = new SparkConf().setAppName("ScalaWordCount").setMaster("local[3]")
    val sc = new SparkContext(conf)
    //    val lines: RDD[String] = sc.textFile(args(0))
    val lines: RDD[String] = sc.textFile("/Users/lionyan/Desktop/SparkDemo/teacher(1).log")

    print(lines.collect())
    //------------------------------------------方法一--------------------------------------------------//
    //切分压平
    val words = lines.flatMap(_.split(" "))

    //将单词和一组合
    val wordAndOne: RDD[(String, Int)] = words.map((_, 1))
    val reduced: RDD[(String, Int)] = wordAndOne.reduceByKey(_ + _)
    val sorted: RDD[(String, Int)] = reduced.sortBy(_._2, false)

    val result: Array[(String, Int)] = sorted.collect()
    //保存结果
    //    sorted.saveAsTextFile("/Users/lionyan/Desktop/SparkDemo/teacher_out3")
    println("rddresult:" + result.toBuffer)


    //------------------------------------------方法二--------------------------------------------------//
    val teacherAndOne = lines.map(line => {
      val index = line.lastIndexOf("/")
      val teacher = line.substring(index + 1)
      (teacher, 1)
    })
    val reduced2: RDD[(String, Int)] = teacherAndOne.reduceByKey(_ + _)
    val sorted2: RDD[(String, Int)] = reduced2.sortBy(_._2, false)
    val result2: Array[(String, Int)] = sorted2.collect()
    println("rddResult2" + result2.toBuffer)


    //--------------------------------------------------------------------------------------------------//
    val sbjectAndTeacher: RDD[((String, String), Int)] = lines.map(line => {
      val index = line.lastIndexOf("/")
      val teacher = line.substring(index + 1)
      val httphost = line.substring(0, index)
      //      val subject = httphost.split("[.]")
      //      val className = subject(0).split("/")(1)

      val subject = new URL(httphost).getHost.split("[.]")(0)

      ((subject, teacher), 1)
    })

    //聚合,学科及老师联合当key
    val reduce3: RDD[((String, String), Int)] = sbjectAndTeacher.reduceByKey(_ + _)
    //    val group3 = reduce3.groupBy(_._1._1)
    val group3 = reduce3.groupBy((t: ((String, String), Int)) => t._1._1, 4)
    val sorted3 = group3.mapValues(_.toList.sortBy(_._2).reverse.take(3))
    val r = sorted3.collect()

    println(r.toBuffer)

    //    释放
    sc.stop()
  }
}
