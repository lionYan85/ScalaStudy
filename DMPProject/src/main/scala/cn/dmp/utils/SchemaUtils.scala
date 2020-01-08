package cn.dmp.utils

import org.apache.spark.sql.types.{StringType, StructField, StructType}

object SchemaUtils {
  val logStructType = StructType(Seq(
    StructField("sessionid", StringType),
    StructField("provincename", StringType)
  ))
}
