package Redis

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import spark.test.GroupIP.MyUtils

object CalculateUtil {

  def calculateTotal(fields: RDD[Array[String]]) = {

    val priceRDD: RDD[Double] = fields.map(arr => {

      val price = arr(4).toDouble
      price
    })
    /*当前批次的总金额*/
    val sum = priceRDD.reduce(_ + _)

    val conn = JedisConnectionPool.getConnention()

    conn.incrByFloat(Constant.TOTAL_INCOME, sum)

    println("total_income:" + sum)

    conn.close()

  }

  def calculateItem(fields: RDD[Array[String]]) = {

    val itemAndPrice: RDD[(String, Double)] = fields.map(arr => {
      //分类
      val item = arr(2)
      //金额
      val price = arr(4).toDouble

      (item, price)
    })

    //需要序列化，然后发送到exector端，方式不好
    //    val conn = JedisConnectionPool.getConnention()
    //按商品分类聚合
    val reduced: RDD[(String, Double)] = itemAndPrice.reduceByKey(_ + _)
    //将当前批次的数据累加到redis中
    reduced.foreachPartition(part => {
      val conn = JedisConnectionPool.getConnention()
      //获取一个jedis链接
      part.foreach(t => {
        conn.incrByFloat(t._1, t._2)

        println("key:" + t._1 + "value:" + t._2)

      })

      conn.close()

    })

  }

  def calculateZone(fields: RDD[Array[String]], broadcastRef: Broadcast[Array[(Long, Long, String)]]) = {

    val provinceAndPrice: RDD[(String, Double)] = fields.map(arr => {
      val ip = arr(1)
      val ipNum = MyUtils.ip2Long(ip)
      val price = arr(4).toDouble
      //在executor中获取广播变量规则
      val allRules: Array[(Long, Long, String)] = broadcastRef.value

      val index = MyUtils.binarySearch(allRules, ipNum)
      var province = "未知"
      if (index != -1) {
        province = allRules(index)._3
      }

      (province, price)
    })

    val reduced: RDD[(String, Double)] = provinceAndPrice.reduceByKey(_ + _)
    reduced.foreachPartition(partion => {

      val conn = JedisConnectionPool.getConnention()
      partion.foreach(t => {
        conn.incrByFloat(t._1, t._2)

        println("key:" + t._1 + "value:" + t._2)

        conn.close()
      })

    })


  }

}
