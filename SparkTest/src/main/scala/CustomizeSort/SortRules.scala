package CustomizeSort

object SortRules {

  implicit object OrderingXianRou extends Ordering[XianRou]{
    override def compare(x: XianRou, y: XianRou): Int = {

      if(x.faceValue == y.faceValue){
        x.age -y.age
      }else{
        y.faceValue - x.faceValue
      }

    }
  }

}
