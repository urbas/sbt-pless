package si.urbas.sbtutils.textfiles

import sbt._
import java.util.regex.Pattern
import java.io.File

object TextFileManipulation {

  def bumpVersionInFile(file: File,
                        groupId: String,
                        artifactId: String,
                        version: String): Unit = {
    replaceTextInFile(file, replaceVersionsInText(_, groupId, artifactId, version))
  }

  private[textfiles] def replaceVersionsInText(content: String,
                                    groupId: String,
                                    artifactId: String,
                                    newVersion: String): String = {
    val quotedGroupId = quoted(groupId)
    val quotedArtifactId = quoted(artifactId)
    val partialSbtCoordinates = s"$quotedGroupId$sbtDelimiter$quotedArtifactId"
    val sbtCoordinatesPattern = s"($partialSbtCoordinates$versionDelimiter)$versionCoordinatePattern"
    val newSbtCoordinates = "$1\"" + newVersion + "\""

    content.replaceAll(
      sbtCoordinatesPattern,
      newSbtCoordinates
    )
  }

  private def replaceTextInFile(file: File,
                                contentTransformer: String => String) {
    val transformedContent = contentTransformer(IO.read(file))
    IO.write(file, transformedContent)
  }

  private def quoted(text: String) = {
    Pattern.quote("\"" + text + "\"")
  }

  private val someSpace = "\\s+"
  private val versionDelimiter = s"$someSpace%$someSpace"
  private val sbtDelimiter = s"$someSpace%%$someSpace"
  private val versionCoordinatePattern = "\"[^\"]+\""
}
