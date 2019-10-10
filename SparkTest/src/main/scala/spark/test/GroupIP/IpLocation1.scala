package spark.test.GroupIP

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object IpLocation1 {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("Iplocation1").setMaster("local[4]")
    val sc = new SparkContext(conf)
    val rules: Array[(Long, Long, String)] = MyUtils.readRules("/Users/lionyan/Desktop/SparkDemo/ip.txt")

//    val rulesLines = sc.textFile("")
//
//    val ipLinesRDD: RDD[(Long, Long, String)] = rulesLines.map(line => {
//      val fields = line.split("[|]")
//
//      val startNum = fields(2).toLong
//      val endNum = fields(3).toLong
//      val province = fields(6)
//      (startNum, endNum, province)
//    })
//    val rulesInDriver: Array[(Long, Long, String)] = ipLinesRDD.collect()
    
    
    val boardcastRef: Broadcast[Array[(Long, Long, String)]] = sc.broadcast(rules)

    val accessLines: RDD[String] = sc.textFile("/Users/lionyan/Desktop/SparkDemo/access.log")

    val func = (line: String) => {
      val fileds = line.split("[|]")
      val ip = fileds(1)
      val ipNum = MyUtils.ip2Long(ip)
      //二分法查找
      val rulesInExecutor: Array[(Long, Long, String)] = boardcastRef.value
      var province = ""
      val index = MyUtils.binarySearch(rulesInExecutor, ipNum)
      if (index != -1) {
        province = rulesInExecutor(index)._3
      }
      (province, 1)
    }

    val provinceAndOne: RDD[(String, Int)] = accessLines.map(func)

    val reduced: RDD[(String, Int)] = provinceAndOne.reduceByKey(_ + _)

    val r = reduced.collect()

    print(r.toBuffer)


  }


}
