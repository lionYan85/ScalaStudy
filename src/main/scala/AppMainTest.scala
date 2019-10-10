import java.io.{FileNotFoundException, IOException, PrintWriter}
import java.net.ServerSocket

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
//import org.apache.spark.streaming.StreamingContext
//import org.scalatest.time.Seconds

class AppMainTest {

//  def main(args: Array[String]): Unit = {

    val textFieldUrl = "/Users/lionyan/Desktop/ScalaStudy/20181022.txt"

    //    PV

    UV

    //    TopN
//  }

}


object PV extends AppMainTest {

  println("hello word")

  //创建conf
  private val sparkConf: SparkConf = new SparkConf().setAppName("PV").setMaster("local[2]")

  //创建context对象
  private val sc: SparkContext = new SparkContext(sparkConf)
  //设置输出的日志级别
  sc.setLogLevel("WARN")
  //读取日志
  private val dataRDD: RDD[String] = sc.textFile("/Users/lionyan/Desktop/ScalaStudy/20181022.txt")
  //方式一：计算有多少行及PV总量
  private val finalResult: Long = dataRDD.count()
  println("result：" + finalResult)

  //方式二：
  //  private val pvOne: RDD[(String, Int)] = dataRDD.map(x => ("PV", 1))
  //  private val resultPV: RDD[(String, Int)] = pvOne.reduceByKey(_ + _)
  //  resultPV.foreach(x => println(x))
  //  sc.stop()
}

object UV extends AppMainTest {

  def main(args: Array[String]): Unit = {

    try {

      val sparkConf: SparkConf = new SparkConf().setAppName("UV").setMaster("local[2]")

      val sparkContext: SparkContext = new SparkContext(sparkConf)

      sparkContext.setLogLevel("WARN")

      val dataRDD: RDD[String] = sparkContext.textFile("/Users/lionyan/Desktop/SparkDemo/20190814.txt")

      val ips: RDD[String] = dataRDD.map(_.split("http")(0))

      val ipNum: Long = ips.distinct().count()

      println(ipNum)
    } catch {
      case ex: FileNotFoundException => {
        println("Missing file exception")
      }
      case ex: IOException => {
        println("IO Exception")
      }
    }
  }
  //  sparkContext.stop()
}

object TopN extends AppMainTest {
  //创建sparkConf对象
  private val sparkConf: SparkConf = new SparkConf().setAppName("PV").setMaster("local[2]")
  //创建SparkContext对象
  private val sc: SparkContext = new SparkContext(sparkConf)
  //设置输出的日志级别
  sc.setLogLevel("WARN")

  try {
    //读取日志数据
    val dataRDD: RDD[String] = sc.textFile("/Users/lionyan/Desktop/SparkDemo/20190814.txt")
    print("RDD")
    //        //对每一行的日志信息进行切分并且过滤清洗掉不符合规则的数据
    //        //通过对日志信息的分析，我们知道按照空格切分后，下标为10的是url，长度小于10的暂且认为是不符合规则的数据
    val urlAndOne: RDD[(String, Int)] = dataRDD.filter(_.split("http").size > 10).map(x => (x.split(" ")(10), 1))
    //        //相同url进行累加
    val result: RDD[(String, Int)] = urlAndOne.reduceByKey(_ + _)
    //        //访问最多的url并进行倒叙排序
    val sortResult: RDD[(String, Int)] = result.sortBy(_._1, false)
    //        //取前五位
    val finalResult: Array[(String, Int)] = sortResult.take(5)
    //    //打印输出
    finalResult.foreach(println)

  } catch {
    case ex: FileNotFoundException => {
      println("Missing file exception")
    }
    case ex: IOException => {
      println("IO Exception")
    }
  }


  sc.stop()
}

object GenerateChar {

  def generateContext(index: Int): String = {
    import scala.collection.mutable.ListBuffer
    val charList = ListBuffer[Char]()
    for (i <- 65 to 90)
      charList += i.toChar
    val charArray = charList.toArray
    charArray(index).toString

  }

  def index = {
    import java.util.Random
    val rdm = new Random()
    rdm.nextInt(7)
  }

  def main(args: Array[String]) {
    val listener = new ServerSocket(9998)
    while (true) {
      val socket = listener.accept()
      new Thread() {
        override def run() = {
          println("socket from" + socket.getInetAddress)
          val out = new PrintWriter(socket.getOutputStream, true)
          while (true) {
            Thread.sleep(500)
            val context = generateContext(index)
            print(context)
            out.write(context)
            out.flush()
          }
          socket.close()
        }
      }.start()
    }
  }
}

//object ScoketStreaming {
//  def main(args: Array[String]){
//    val conf = new SparkConf().setMaster("local[2]").setAppName("socketStreaming")
//    val sc = new StreamingContext(conf,Seconds(2))
//    val lines = sc.socketTextStream("master", 9998)
//    val words = lines.flatMap(_.split(" "))
//    val wordCount = words.map(x => (x, 1)).reduceByKey(_ + _);
//    wordCount.print()
//    sc.start()
//    sc.awaitTermination()
//  }
//}