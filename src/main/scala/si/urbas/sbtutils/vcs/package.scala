package si.urbas.sbtutils

import si.urbas.sbtutils.releases._
import sbt.{IO, Project, State}
import sbtrelease.ReleasePlugin.ReleaseKeys

package object vcs {
  def addFileToVcs(file: sbt.File): ReleaseFunction = {
    state: State => {
      val vcs = Project.extract(state).get(ReleaseKeys.versionControlSystem).get
      val base = vcs.baseDir
      val relativePath = IO.relativize(base, file).get
      vcs.add(relativePath)
      state
    }
  }
}
