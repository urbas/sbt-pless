package si.urbas.sbtutils.releases

import com.typesafe.sbt.pgp.PgpKeys
import sbtrelease.ReleasePlugin.ReleaseKeys
import sbtrelease._
import sbt._
import xerial.sbt.Sonatype.SonatypeKeys

object ReleaseSteps {

//  lazy val addReadmeFileToVcs: ReleaseStep = addFileToVcs(readmeMdFile)

  lazy val publishSigned: ReleaseStep = releaseTask(PgpKeys.publishSigned)

  lazy val sonatypeRelease: ReleaseStep = releaseTask(SonatypeKeys.sonatypeReleaseAll)

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