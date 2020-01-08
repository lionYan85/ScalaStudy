package cn.dmp.utils

import com.esotericsoftware.kryo.util.Util

object RptUtils {

  def caculateReq(reqMode: Int, proNode: Int): List[Double] = {
    if (reqMode == 1 && proNode == 1) {
      List[Double](1, 0, 0)
    } else if (reqMode == 1 && proNode == 2) {
      List[Double](1, 1, 0)
    } else if (reqMode == 1 && proNode == 3) {
      List[Double](1, 1, 1)
    } else {
      List[Double](0, 0, 0)
    }
  }

  def caculateRtb(effTive: Int, billing: Int, bid: Int, orderid: Int, win: Int, winPrice: Double, adPayMent: Double)
  : List[Double]
  = {
    if (effTive == 1 && billing == 1 && bid == 1 && orderid != 0) {
      List[Double](1, 0, 0, 0)
    } else if (effTive == 1 && billing == 1 && bid == 1) {
      List[Double](0, 1, winPrice / 1000.0, adPayMent / 1000.0)
    } else {
      List[Double](0, 0, 0, 0)
    }

  }

  def caculateShowClick(reqMode: Int, effTive: Int): List[Double] = {
    if (reqMode == 2 && effTive == 1) {
      List[Double](1, 0)
    }
    else if (reqMode == 3 && effTive == 1) {
      List[Double](0, 1)
    } else {
      List[Double](0, 0)
    }
  }

}
