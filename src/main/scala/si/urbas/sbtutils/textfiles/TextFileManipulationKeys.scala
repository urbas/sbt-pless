package si.urbas.sbtutils.textfiles

import sbt._

object TextFileManipulationKeys {
   lazy val bumpVersionInReadmeMd = taskKey[Unit]("Replaces any references to the version of this project in 'README.md'.")
   lazy val bumpVersionInFile = inputKey[Unit]("Replaces any references to the version of this project in the given file.")
}
