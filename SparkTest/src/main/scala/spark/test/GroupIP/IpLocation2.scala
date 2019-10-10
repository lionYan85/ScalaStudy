package spark.test.GroupIP

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

object IpLocation2 {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("Iplocation2").setMaster("local[4]")
    val sc = new SparkContext(conf)
//    val rules: Array[(Long, Long, String)] = MyUtils.readRules("/Users/lionyan/Desktop/SparkDemo/ip.txt")

    val rulesLines = sc.textFile("/Users/lionyan/Desktop/SparkDemo/ip.txt")

    val ipLinesRDD: RDD[(Long, Long, String)] = rulesLines.map(line => {
      val fields = line.split("[|]")

      val startNum = fields(2).toLong
      val endNum = fields(3).toLong
      val province = fields(6)
      (startNum, endNum, province)
    })
    val rulesInDriver: Array[(Long, Long, String)] = ipLinesRDD.collect()


    val boardcastRef: Broadcast[Array[(Long, Long, String)]] = sc.broadcast(rulesInDriver)

    val accessLines: RDD[String] = sc.textFile("/Users/lionyan/Desktop/SparkDemo/access.log")

//    val func = (line: String) => {
//      val fileds = line.split("[|]")
//      val ip = fileds(1)
//      val ipNum = MyUtils.ip2Long(ip)
//      //二分法查找
//      val rulesInExecutor: Array[(Long, Long, String)] = boardcastRef.value
//      var province = ""
//      val index = MyUtils.binarySerch(rulesInExecutor, ipNum)
//      if (index != -1) {
//        province = rulesInExecutor(index)._3
//      }
//      (province, 1)
//    }

    val provinceAndOne: RDD[(String, Int)] = accessLines.map(log => {
      val fields = log.split("[|]")
      val ip = fields(1)
      val ipNum = MyUtils.ip2Long(ip)

      val rulesInExecutor: Array[(Long, Long, String)] = boardcastRef.value
      var province = "未知"
      val index = MyUtils.binarySearch(rulesInExecutor, ipNum)
      if (index != -1) {
        province = rulesInExecutor(index)._3
      }
      (province, 1)
    })

    val reduced: RDD[(String, Int)] = provinceAndOne.reduceByKey(_ + _)

//    val r = reduced.collect()
//
//    print(r.toBuffer)


    reduced.foreachPartition(it => {

      val conn: Connection = DriverManager.getConnection("jdbc:mysql://172.16.12.10:3306/bigdata?characterEncoding=UTF-8", "root", "Abc@1234")
      //添加数据到数据库
      val pstm:PreparedStatement = conn.prepareStatement("INSERT INTO logs (province,counts) VALUES (?,?)")

      it.foreach(tp =>{
        pstm.setString(1,tp._1.toString)
        pstm.setInt(2,tp._2)
        println("province" + tp._1.toString)
        pstm.executeUpdate()
      })

      pstm.close()
      conn.close()

    })


  }
}
