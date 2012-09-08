Scala における圏論
==================

目次
----

0. はじめに
1. 関数型言語におけるモナドの役割
2. 圏論について
3. Scala におけるモナド
4. 補足
5. 参考資料

- - - - -

### はじめに

今回の目標は以下の通りです。

* 数学的に圏論の定義を述べ，モナドを定義する。
* 圏論における概念の幾つかを Scala へ翻訳する。
* Scala の標準ライブラリに含まれる幾つかのクラスがモナドであることを確かめる。

- - - - -

### 関数型言語におけるモナドの役割

* 値を保持するコンポーネント
* 計算を合成するための枠組み
* 純粋関数型言語において副作用を扱うための仕組み
  -> なので，Scala においては，ある特定の性質を持ったクラスぐらいの認識で良さそう?

- - - - -

### 圏論について

* 圏
* 関手
* 自然変換
* モナド
* Kleisli トリプル

補足: モノイド

- - - - -

### Scala におけるモナド

以下では，圏論で定義した概念を Scala を使用して記述するが，
言語依存であるため実装者により形式は異なりうる。  
参考: [scalaz.Functor][scalaz.Functor]

* ファンクター（関手）
  以下の定義と同等の構造を持っているクラスで，ファンクター則を満たすもの。
  ```scala
    trait M[A] {
      def map[B](f: A => B): M[B]
    }
  ```

  ファンクター則  
  F1. m map identity == m  
  F2. m map f map g == m map { x => g(f(x)) }  

* モナド
  以下のようなインターフェイスを持ち，モナド則を満たすもの。
  ```scala
    trait M[A] {
      def unit(a: A): M[A]
      def flatten[B]: immutable.Seq[B] // scala.collection.immutable.List より
    }
  ```

  ただし，実際の実装においては，以下で述べる Keisli トリプルからモナドを構成する
  方法が採用されることが多い(と思われる)。  
  余談:  
    以下の定義が採用される理由を圏論的に述べると，自然変換の合成の話から，
    射の合成の話へ落とし込めるからと思われる。

* Keisli トリプルからの構成 
  ```scala
  trait M[A] {
    def unit(a: A): M[A]
    def flatMap[B](f: A => M[B]): M[B]
  }
  ```

  モナド則(Keisli トリプル)  
  M1. m flatMap unit == m     // 両辺の型は，M[A]  
  M2. unit(x) flatMap f == f(x)     // 両辺の方は，M[B]  
  M3. m flatMap f flatMap g == m flatMap { x => f(x) flatMap g } // 両辺の型は，M[C]  

* モナドはファンクター
  map, unit を以下のように定義することにより，モナドはファンクターであることが示される。

    def map[B](f: A => B): M[B] = flatMap { (x: A) => unit(f(x)) }  
    def unit(x: A): M[A] = new M(x)  

  F1 を満たすこと  
  証明.  
    m map { x => identity(x) } = m flatMap { x => unit(identity(x)) } // 定義より  
                               = m // M1 より  

  F2 を満たすこと  
  証明.  
    m map f map g = m flatMap { x => unit(f(x)) } flatMap { y => unit(g(y)) } // 定義より  
                  = m flatMap { x => unit(f(x)) flatMap { y => unit(g(y)) } } // M3より  
                  = m flatMap { x => unit(g(f(x))) } // M2より  
                  = m map { x => g(f(x))} // 定義より  

* モナド則を満たす具体例
    * Option
    * List
-> Scala Check を使って，(部分的に)証明する。

* モナドを知ると便利なこと
    * モナドであることがわかれば，そのクラスの挙動について，既知のクラスからの類推が可能。
    * 抽象論で成立していることを言語に落とし込むことができる。少なくとも，その可能性がある。
    * 自作クラスで for 式のサポート
      for ~ yield は map に変換される。
      多重ループは map と flatMap の組み合わせに変換される。

- - - - -

### 補足

* もっと厳密に圏論の翻訳を知りたい場合は Haskell をやったほうがよさそう。
* Scalaz という Haskell 由来のライブラリが存在する。
  Functor クラスとかある。

- - - - -

### 参考資料

* [モナドのすべて][all-about-monads]
* [Option.flatten should return Option, not List][option-flatten]
* [scalaz.Functor][scalaz.Functor]
* [圏論の基礎][categories]
* [「モナドは象だ(Monads are Elephants)」日本語訳][monads-are-elephants]
* [モナドはメタファーではない][monads-are-not-metaphors]
* [モナドへの近道・Haskell からの寄道][monado-to-haskell]
* [WIKIBOOKS Haskell/圏論][haskell-book]
* [モナドメモ - 圏論におけるモナドの定義][monado-memo]
* [Scala で圏論入門][introduction-to-category-theory-in-cala]
* [はじめての圏論 その第1歩：しりとりの圏][introduction-to-category-theory]

[all-about-monads]: http://www.sampou.org/haskell/a-a-monads/html/ 
[option-flatten]: https://issues.scala-lang.org/browse/SI-4474
[scalaz.Functor]: http://scalaz.github.com/scalaz/scalaz-2.9.0-1-6.0/doc.sxr/scalaz/Functor.scala.html
[categories]: http://www.amazon.co.jp/%E5%9C%8F%E8%AB%96%E3%81%AE%E5%9F%BA%E7%A4%8E-S-%E3%83%9E%E3%83%83%E3%82%AF%E3%83%AC%E3%83%BC%E3%83%B3/dp/4621063243/ref=dp_ob_title_bk
[monads-are-elephants]: http://dl.dropbox.com/u/261418/Monads_are_Elephants/index.html 
[monads-are-not-metaphors]: http://eed3si9n.com/ja/monads-are-not-metaphors
[monado-to-haskell]: http://kurt.scitec.kobe-u.ac.jp/~shg/SFCArchives/docs/shortcut_monad.pdf
[haskell-book]: http://ja.wikibooks.org/wiki/Haskell/%E5%9C%8F%E8%AB%96
[monado-memo]: http://www.sharp-bang.jp/prog/monad_memo.html
[introduction-to-category-theory-in-cala]: https://github.com/scalajp/introduction-to-category-theory-in-scala-jp/wiki
[introduction-to-category-theory]: http://d.hatena.ne.jp/m-hiyama/20060821/1156120185

