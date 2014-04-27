package si.urbas.sbtutils

import sbt._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbt.complete.Parsers._
import sbt.Keys._

package object vcs {
  lazy val addFileToVcs = inputKey[Unit]("Stages the given file into the VCS.")

  lazy val tasks = {
    Seq[Def.Setting[_]](
      addFileToVcs := addFileToVcsImpl(state.value, fileParser(file(".")).parsed)
    )
  }

  def addFileToVcsImpl(state: State, fileToAdd: File): Unit = {
    Project.extract(state).get(versionControlSystem) match {
      case Some(vcs) =>
        val relativePath = IO.relativize(vcs.baseDir, fileToAdd).get
        vcs.add(relativePath) !! state.log
      case None =>
        throw new RuntimeException(s"Cannot add file '$fileToAdd' to the VCS. No VCS found.")
    }
  }
}
