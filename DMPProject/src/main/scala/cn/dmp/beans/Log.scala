package cn.dmp.beans

class Log(val sessionid: String,
          val provincename: String,
          val appname: String,
          val appid: String,
          val requestmode: Int,
          val processnode: Int,
          val iseffective: Int,
          val isbilling: Int,
          val isbid: Int,
          val adorderid: Int,
          val iswin: Int,
          val winprice: Double,
          val adpayment: Double
         ) extends Product with Serializable {


  override def productElement(n: Int): Any = n match {
    case 0 => sessionid
  }

  //  对象有多少个成员属性
  override def productArity: Int = 14

  //  比较两个对象是否同一个对象
  override def canEqual(that: Any): Boolean = that.isInstanceOf[Log]
}

object Log {
  def apply(array: Array[String]): Log = new Log(
    array(0),
    array(6),
    array(1), //appname 无数据  暂定为 1
    array(2), //appid
    array(10).toInt,
    array(11).toInt, //appname 无数据  暂定为 1
    array(12).toInt,
    array(13).toInt, //appname 无数据  暂定为 1
    array(14).toInt,
    array(15).toInt, //appname 无数据  暂定为 1
    array(13).toInt, //appname 无数据  暂定为 1
    array(14).toDouble,
    array(15).toDouble, //appname 无数据  暂定为 1

  )
}
