package spark.test

import java.net.URL

import org.apache.spark.{Partitioner, SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

import scala.collection.mutable

object GroupFavTeacher {
  def main(args: Array[String]): Unit = {
    //设置应用程序名
    val conf = new SparkConf().setAppName("ScalaWordCount").setMaster("local[3]")
    val sc = new SparkContext(conf)
    //    val lines: RDD[String] = sc.textFile(args(0))
    val lines: RDD[String] = sc.textFile("/Users/lionyan/Desktop/SparkDemo/teacher(1).log")

    print(lines.collect())

    val sbjectAndTeacher: RDD[((String, String), Int)] = lines.map(line => {
      val index = line.lastIndexOf("/")
      val teacher = line.substring(index + 1)
      val httphost = line.substring(0, index)
      val subject = new URL(httphost).getHost.split("[.]")(0)

      ((subject, teacher), 1)
    })

    //聚合,学科及老师联合当key
    val reduce: RDD[((String, String), Int)] = sbjectAndTeacher.reduceByKey(_ + _)

    //计算学科数
    val distinctRdd = reduce.map(_._1._1).distinct().collect()

    val sbPartitioner = new SubjectParitioner(distinctRdd)

    //自定义分区器,并且按照指定的分区器分区
    val partitioned: RDD[((String, String), Int)] = reduce.partitionBy(sbPartitioner)

    val sorted: RDD[((String, String), Int)] = partitioned.mapPartitions(it => {
      it.toList.sortBy(_._2).take(3).iterator

    })
    val r: Array[((String, String), Int)] = sorted.collect()

    println(r.toBuffer)

    sorted.saveAsTextFile("/Users/lionyan/Desktop/SparkDemo/FaveTeacher")

    //    释放
    sc.stop()
  }
}


//自定义分区器
class SubjectParitioner(sbs: Array[String]) extends Partitioner {

  val rules = new mutable.HashMap[String, Int]()

  var i = 0

  for (sb <- sbs) {
    rules.put(sb, i)
    i += 1
  }

  //分区的数量
  override def numPartitions: Int = sbs.length

  /*根据key计算分区编号
  * key为一个元组
  * */
  override def getPartition(key: Any): Int = {
    //获取学科名称
    val subject = key.asInstanceOf[(String, String)]._1
    //根据规则计算分区编号
    rules(subject)
  }
}