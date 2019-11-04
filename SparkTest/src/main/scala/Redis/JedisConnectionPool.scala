package Redis

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

object JedisConnectionPool {

  val config = new JedisPoolConfig()
  config.setMaxTotal(20)
  config.setMaxIdle(10)
  config.setTestOnBorrow(true)

  val pool = new JedisPool(config, "172.16.12.10", 6379, 10000)

  def getConnention(): Jedis = {
    pool.getResource
  }

  def main(args: Array[String]): Unit = {

    val conn = JedisConnectionPool.getConnention()

//    conn.set("income", "1000")
//
//    var rr = conn.get("income")
//    println(rr)
//
//    val r1 = conn.get("xiaoniu")
//
//    println(r1)
//
//    conn.incrBy("xiaoniu", 50)
//
//    val r2 = conn.get("xiaoniu")
//
//    println(r2)
//
    val r = conn.keys("*")
    import scala.collection.JavaConversions._
    for (p <- r) {
      println(p + " : " + conn.get(p))
    }

  }

}
