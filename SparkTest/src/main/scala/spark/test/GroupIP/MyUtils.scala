package spark.test.GroupIP

import scala.io.{BufferedSource, Source}

object MyUtils {

  def ip2Long(ip: String): Long = {
    val fragments = ip.split("[.]")
    var ipNum = 0L
    for (i <- 0 until fragments.length) {
      ipNum = fragments(i).toLong | ipNum << 8L
    }
    ipNum
  }

  def readRules(path: String): Array[(Long, Long, String)] = {

    val bf: BufferedSource = Source.fromFile(path)
    val lines: Iterator[String] = bf.getLines()
    val rules: Array[(Long, Long, String)] = lines.map(line => {
      val fileds = line.split("[|]")
      val startNum = fileds(2).toLong
      val endNum = fileds(3).toLong
      val province = fileds(6)
      (startNum, endNum, province)
    }).toArray
    rules

  }

  //二分法查找
  def binarySearch(lines: Array[(Long, Long, String)], ip: Long): Int = {
    var low = 0
    var high = lines.length - 1
    while (low <= high) {
      val middle = (low + high) / 2
      if ((ip >= lines(middle)._1) && (ip <= lines(middle)._2))
        return middle
      if (ip < lines(middle)._1)
        high = middle - 1
      if (ip > lines(middle)._2)
        low = middle + 1
    }
    -1
  }


  def main(args: Array[String]): Unit = {

    val rules: Array[(Long, Long, String)] = readRules("/Users/lionyan/Desktop/SparkDemo/ip.txt")

    val ipNum = ip2Long("111.198.38.185")
    /*查找*/
    val index = binarySearch(rules, ipNum)
    /*根据角标到rules查找对应数据*/
    println(index)
    if (index != -1) {
      val tp = rules(index)
      val province = tp._3
      print(province)
    } else {
      print("未知地区")
    }
  }

}
