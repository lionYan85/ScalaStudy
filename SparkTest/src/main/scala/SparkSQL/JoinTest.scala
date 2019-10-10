package SparkSQL

import org.apache.spark.sql._

object JoinTest {

  def main(args: Array[String]): Unit = {

    /*spark2.x SQL执行入口*/
    val spark = SparkSession.builder()
      .appName("JoinTest")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    val lines: Dataset[String] = spark.createDataset(List("1,laozhao,china", "2,laoduan,usa","3,laoyang,english"))

    val tpDs = lines.map(line => {
      val fields = line.split(",")
      val id = fields(0).toLong
      val name = fields(1)
      val nationCode = fields(2)
      (id, name, nationCode)
    })
    val df1 = tpDs.toDF("id", "name", "nation")


    val nations: Dataset[String] = spark.createDataset(List("china,中国", "usa,美国"))
    val nationDs: Dataset[(String, String)] = nations.map(nation => {

      val fields = nation.split(",")
      val ename = fields(0)
      val cname = fields(1)
      (ename, cname)
    })
    val df2 = nationDs.toDF("ename", "cname")

    //关联表
    //第一种，创建视图
    //    df1.createTempView("v_users")
    //    df2.createTempView("v_nations")
    //    val frame: DataFrame = spark.sql("select name,cname from v_users join v_nations on nation = ename")
    //    frame.show()

    //第二种
    val dataFrame = df1.join(df2, $"nation" === $"ename", "left_outer")
    dataFrame.show()
    spark.stop()
  }
}
