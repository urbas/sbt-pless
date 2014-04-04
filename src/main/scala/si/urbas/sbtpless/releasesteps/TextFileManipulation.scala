package si.urbas.sbtpless.releasesteps

import si.urbas.sbtpless._
import java.util.regex.Pattern
import sbt._

object TextFileManipulation {

  lazy val bumpVersionInReadmeMd = inputKey[Unit]("Replaces any references to the version of this project in 'README.md'.")

  lazy val tasks: Seq[Def.Setting[_]] = Seq(
    bumpVersionInReadmeMd := {
      bumpVersionInReadmeFile("gla", "dal")
    }
  )

  def bumpVersionInReadmeFile(groupId: String, artifactId: String): Unit = {
    println(s"Hello: $groupId % $artifactId")
  }

  def replaceVersionsInText(content: String,
                            groupId: String,
                            artifactId: String,
                            newVersion: String): String = {
    val quotedGroupId = quoted(groupId)
    val quotedArtfactId = quoted(artifactId)
    val partialSbtCoordinates = s"$quotedGroupId$sbtDelimiter$quotedArtfactId"
    val sbtCoordinatesPattern = s"($partialSbtCoordinates$versionDelimiter)$versionCoordinatePattern"
    val newSbtCoordinates = "$1\"" + newVersion + "\""

    content.replaceAll(
      sbtCoordinatesPattern,
      newSbtCoordinates
    )
  }

  def replaceTextInFile(file: File, contentTransformer: String => String) {
    val transformedContent = contentTransformer(IO.read(file))
    IO.write(file, transformedContent)
  }

  def replaceTextInFile(file: sbt.File, regexPattern: String, replacementPattern: State => String): BuildFunction = {
    state: State => {
      val transformedContent = IO.read(file).replaceAll(
        regexPattern,
        replacementPattern(state)
      )
      IO.write(file, transformedContent)
      state
    }
  }

  private def quoted(text: String) = {
    Pattern.quote("\"" + text + "\"")
  }

  private val someSpace = "\\s+"
  private val versionDelimiter = s"$someSpace%$someSpace"
  private val sbtDelimiter = s"$someSpace%%$someSpace"
  private val versionCoordinatePattern = "\"[^\"]+\""
}
