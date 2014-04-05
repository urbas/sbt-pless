package si.urbas.sbtutils.textfiles

import sbt._
import sbt.Keys._
import java.util.regex.Pattern
import sbt.complete.Parsers._
import sbt.complete.Parser._
import java.nio.file.{Path, Files}
import java.io.File
import sbt.File
import sbt.complete.Parser

object TextFileManipulation {

  lazy val tasks = Seq[Def.Setting[_]](
    TextFileManipulationKeys.bumpVersionInReadmeMd := bumpVersionInFile(readmeMdFile, organization.value, name.value, version.value),
    TextFileManipulationKeys.bumpVersionInFile := {
      val fileName = fileNameParser.parsed
      println(s"Hello $fileName")
      //      bumpVersionInFile(file(fileName), organization.value, name.value, version.value)
    }
  )

  private def bumpVersionInFile(file: File, groupId: String, artifactId: String, version: String): Unit = {
    replaceTextInFile(file, replaceVersionsInText(_, groupId, artifactId, version))
  }

  private def replaceVersionsInText(content: String,
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

  private def replaceTextInFile(file: File, contentTransformer: String => String) {
    val transformedContent = contentTransformer(IO.read(file))
    IO.write(file, transformedContent)
  }

  private def quoted(text: String) = {
    Pattern.quote("\"" + text + "\"")
  }

  def filesIterator(directory: Path, filter: Path => Boolean = _ => true): Iterable[Path] = {
    import scala.collection.JavaConversions._
    val subPathStream = Files.newDirectoryStream(directory).toStream
    val (subDirectories, filesOnly) = subPathStream.partition(path => Files.isDirectory(path))
    filesOnly ++ subDirectories.flatMap(filesIterator(_, filter))
  }

  def fileSet(base: File = file("."), maxNumberOfFiles: Int = 25): Set[String] = {
    val basePath = base.toPath
    filesIterator(basePath)
      .map(basePath.relativize)
      .map(_.toString)
      .take(maxNumberOfFiles)
      .toSet
  }

  def fileParser(base: File): Parser[File] = OptSpace ~> StringBasic.map(file).examples(fileSet(maxNumberOfFiles = 25))

  private val fileNameParser = {
    OptSpace ~> StringBasic
      .map(file)
      .filter(_.isFile, str => s"The file '$str'. Does not exist.")
      .examples(fileSet())
  }

  private val someSpace = "\\s+"
  private val versionDelimiter = s"$someSpace%$someSpace"
  private val sbtDelimiter = s"$someSpace%%$someSpace"
  private val versionCoordinatePattern = "\"[^\"]+\""
  private lazy val readmeMdFile: File = file("README.md")
}
