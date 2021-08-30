object Test {
  def main(args: Array[String]): Unit = {
    val xs = Seq("john 1", "mary 2", "alice 3", "bob 4")
    println(xs)

    val xs2 = xs.map(x => x.split(" "))
    println(xs2)

    val xs3 = xs.flatMap(x => x.split(" "))
    println(xs3)

    val p=List(("hello",35,1.50),("nihao",36,1.78))
    println(p.map(_._1))
  }
}
