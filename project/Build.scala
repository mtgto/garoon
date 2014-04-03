import sbt._
import Keys._

object Build extends Build {
  override lazy val settings = super.settings ++ Seq(
    fork := true
  )
}
