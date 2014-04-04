package si.urbas

import sbt._

package object sbtpless {
  type BuildFunction = State => State

  lazy val readmeMdFile: sbt.File = {
    file("README.md")
  }
}
