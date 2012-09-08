import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object ListCheck extends Properties("List") {
  // ===========================
  // ファンクター則の確認
  // ===========================

  // m map id == m
  property("F1 : Int") = forAll { (a: Int) =>
    val id = identity[Int] _
    ( List(a) map id ) == List(a)
  }
  property("F1 : String") = forAll { (a: String) =>
    val id = identity[String] _
    ( List(a) map id ) == List(a)
  }

  // m map f map g == m map f compose g
  property("F2 : Int, f: Int => String, g: String => Boolean") = forAll { (a: Int, f: Int => String, g: String => Boolean) =>
    ( List(a) map f map g ) == ( List(a) map { g compose f } )
  }
  property("F2 : Ex") = forAll { (a: Int) =>
    val f = (x: Int) => x.toString
    val g = (y: String) => y match { case "0" => false case _ => true }
    ( List(a) map f map g ) == ( List(a) map { g compose f } )
  }

  // ===========================
  // モナド則の確認
  // ===========================

  // m flatMap unit == m
  property("M1 : Int") = forAll { (a: Int) =>
    List(a).flatMap { x => List(x) } == List(a)
  }
  property("M1 : String") = forAll { (a: String) =>
    List(a).flatMap { x => List(x) } == List(a)
  }

  // unit(x) flatMap f == f
  property("M2 : Int, f: Int => List[String]") = forAll { (a: Int, f: Int => List[String]) =>
    ( List(a) flatMap { y => f(y)} ) == f(a)
  }
  property("M2 : String, f: String => List[Int]") = forAll { (a: Int, f: Int => List[String]) =>
    ( List(a) flatMap { y => f(y)} ) == f(a)
  }

  // m flatMap f flatMap g == m flatMap { x => f(x) flatMap g }
  property("M3") = forAll { (a: Int, f: Int => List[String], g: String => List[Boolean]) =>
    ( List(a) flatMap { x => f(x) } flatMap { y => g(y) } ) == ( List(a) flatMap { x => f(x) flatMap g } )
  }
}
