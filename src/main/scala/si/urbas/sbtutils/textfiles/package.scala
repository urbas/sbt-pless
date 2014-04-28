package si.urbas.sbtutils

import sbt._
import sbt.Keys._
import java.io.File
import si.urbas.sbtutils.textfiles.TextFileManipulation._
import si.urbas.sbtutils.vcs._

package object textfiles {
  val README_MD_FILENAME = "README.md"

  lazy val readmeMdFile = settingKey[File]("the project's README.md file.")
  lazy val bumpVersionInReadmeMd = taskKey[Unit]("Replaces any references to the version of this project in 'README.md'.")
  lazy val addReadmeFileToVcs = taskKey[Unit]("Stages the 'README.md' into git.")

  lazy val tasks = Seq[Def.Setting[_]](
    readmeMdFile := baseDirectory.value / README_MD_FILENAME,
    bumpVersionInReadmeMd := bumpVersionInFile(readmeMdFile.value, organization.value, name.value, version.value),
    addReadmeFileToVcs := addFileToVcsImpl(state.value, readmeMdFile.value)
  )
}
