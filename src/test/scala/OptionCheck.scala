import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object OptionCheck extends Properties("Option") {
  // ===========================
  // ファンクター則の確認
  // ===========================

  // m map id == m
  property("F1 : Int") = forAll { (a: Int) =>
    val id = identity[Int] _
    ( Some(a) map id ) == Some(a)
  }
  property("F1 : String") = forAll { (a: String) =>
    val id = identity[String] _
    ( Some(a) map id ) == Some(a)
  }

  // m map f map g == m map f compose g
  property("F2 : Int, f: Int => String, g: String => Boolean") = forAll { (a: Int, f: Int => String, g: String => Boolean) =>
    ( Some(a) map f map g ) == ( Some(a) map { g compose f } )
  }
  property("F2 : Ex") = forAll { (a: Int) =>
    val f = (x: Int) => x.toString
    val g = (y: String) => y match { case "0" => false case _ => true }
    ( Some(a) map f map g ) == ( Some(a) map { g compose f } )
  }

  // ===========================
  // モナド則の確認
  // ===========================

  // m flatMap unit == m
  property("M1 : Int") = forAll { (a: Int) =>
    Some(a).flatMap { x => Some(x) } == Some(a)
  }
  property("M1 : String") = forAll { (a: String) =>
    Some(a).flatMap { x => Some(x) } == Some(a)
  }

  // unit(x) flatMap f == f
  property("M2 : Int, f: Int => Option[String]") = forAll { (a: Int, f: Int => Option[String]) =>
    ( Some(a) flatMap { y => f(y)} ) == f(a)
  }
  property("M2 : String, f: String => Option[Int]") = forAll { (a: Int, f: Int => Option[String]) =>
    ( Some(a) flatMap { y => f(y)} ) == f(a)
  }

  // m flatMap f flatMap g == m flatMap { x => f(x) flatMap g }
  property("M3") = forAll { (a: Int, f: Int => Option[String], g: String => Option[Boolean]) =>
    ( Some(a) flatMap { x => f(x) } flatMap { y => g(y) } ) == ( Some(a) flatMap { x => f(x) flatMap g } )
  }
}
