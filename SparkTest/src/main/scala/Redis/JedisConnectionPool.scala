package Redis

import java.util
import redis.clients.jedis.{HostAndPort, Jedis, JedisPool, JedisPoolConfig}
import redis.clients.jedis.{JedisCluster, _}
import scala.collection.mutable

object JedisConnectionPool {

  val config = new JedisPoolConfig()
  config.setMaxTotal(20)
  config.setMaxIdle(10)
  config.setTestOnBorrow(true)

  //  单机pool
  val pool = new JedisPool(config, "172.16.12.10", 6379, 10000)

  //集群redis
  val jedisClusterNodes = new util.HashSet[HostAndPort]()
  jedisClusterNodes.add(new HostAndPort("172.16.12.10", 6379))
  jedisClusterNodes.add(new HostAndPort("172.16.12.11", 6379))
  jedisClusterNodes.add(new HostAndPort("172.16.12.12", 6379))
  jedisClusterNodes.add(new HostAndPort("172.16.12.10", 7000))
  jedisClusterNodes.add(new HostAndPort("172.16.12.10", 7001))
  jedisClusterNodes.add(new HostAndPort("172.16.12.10", 7002))

  val jedisCluster = new JedisCluster(jedisClusterNodes)


  def getConnention(): Jedis = {
    pool.getResource
  }

  def main(args: Array[String]): Unit = {

    //    val conn = JedisConnectionPool.getConnention()

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
    //    val r = conn.keys("*")
    import scala.collection.JavaConversions._
    //    for (p <- r) {
    //      println(p + " : " + conn.get(p))
    //    }

    val str = jedisCluster.get("*")
    println(str)
    import scala.collection.JavaConversions._
    //    for (p: Char <- str) {
    //      println(p + " : " + jedisCluster.get(p.toString))
    //    }

  }

}
