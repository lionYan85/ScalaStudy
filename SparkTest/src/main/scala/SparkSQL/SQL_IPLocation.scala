package SparkSQL

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import spark.test.GroupIP.MyUtils

object SQL_IPLocation {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("JoinTest")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    val IPData: Dataset[(Long, Long, String)] = spark.createDataset(MyUtils.readRules
    ("/Users/lionyan/Desktop/SparkDemo/ip.txt").toList)

    val ruleDataFrame: DataFrame = IPData.toDF("startIP", "endIP", "province")

    val access = spark.read.textFile("/Users/lionyan/Desktop/SparkDemo/access.log")
    val accessLogs: Dataset[Long] = access.map(log => {
      val fields = log.split("[|]")
      val ip = fields(1)
      val ipNum = MyUtils.ip2Long(ip)
      (ipNum)
    })
    val IPDataFrame: DataFrame = accessLogs.toDF("ipNum")

    ruleDataFrame.createTempView("v_rules")

    IPDataFrame.createTempView("v_ips")

    val r = spark.sql("select province, count(*)counts from v_ips  join v_rules  on (ipNum >= startIP and " +
      "ipNum <= endIP) group by province")

    r.show()

  }


}
