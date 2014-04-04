package si.urbas.sbtpless

import com.typesafe.sbt.pgp.PgpKeys
import sbtrelease.ReleasePlugin.ReleaseKeys
import sbtrelease.ReleasePlugin.ReleaseKeys.versions
import sbtrelease._
import sbt._
import xerial.sbt.Sonatype.SonatypeKeys

object PlessReleaseSteps {

  private lazy val readmeMdFile: sbt.File = {
    file("README.md")
  }

  lazy val bumpVersionInReadmeFile: ReleaseStep = replaceTextInFile(
    readmeMdFile,
    regexPattern = "(\"si\\.urbas\" %% \"pless\" %) \"([^\"]+)\"",
    replacementPattern = state => "$1 \"" + state.get(versions).get._1 + "\""
  )

  lazy val addReadmeFileToVcs: ReleaseStep = addFileToVcs(readmeMdFile)

  lazy val publishSigned: ReleaseStep = releaseTask(PgpKeys.publishSigned)

  lazy val sonatypeRelease: ReleaseStep = releaseTask(SonatypeKeys.sonatypeReleaseAll)

  def replaceTextInFile(file: sbt.File, regexPattern: String, replacementPattern: State => String): BuildFunction = {
    state: State => {
      val transformedContent = IO.read(file).replaceAll(
        regexPattern,
        replacementPattern(state)
      )
      IO.write(readmeMdFile, transformedContent)
      state
    }
  }

  def addFileToVcs(file: sbt.File): BuildFunction = {
    state: State => {
      val vcs = Project.extract(state).get(ReleaseKeys.versionControlSystem).get
      val base = vcs.baseDir
      val relativePath = IO.relativize(base, file).get
      vcs.add(relativePath)
      state
    }
  }
}