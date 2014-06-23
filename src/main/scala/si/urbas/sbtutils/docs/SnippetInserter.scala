package si.urbas.sbtutils.docs

import java.io.FileNotFoundException
import java.util.regex.Pattern

import sbt._
import si.urbas.sbtutils.docs.SnippetInserter._

import scala.collection.mutable

class SnippetInserter(snippetSearchPaths: Iterable[File]) {
  def snippet(sourceFile: String, snippetName: String, lineTransformer: String => String = identity, snippetTransformer: String => String = identity): String = {
    val snippetFile = findSnippetFile(sourceFile)
    if (snippetFile.isFile) {
      val snippetLines = linesWithinSnippet(IO.readLines(snippetFile), snippetName)
      snippetTransformer(concatenateSnippetLines(snippetLines, lineTransformer))
    } else {
      throw new FileNotFoundException(insertionErrorMessage(snippetName, sourceFile) + s" Could not find the file in the search paths: ${snippetSearchPaths.mkString(", ")}.")
    }
  }


  def prefixLine(prefix: String)(line: String): String = {
    s"$prefix$line"
  }

  def trimPrefixLine(prefix: String)(line: String): String = {
    prefixLine(prefix)(line.trim)
  }

  def stripIndentation(lines: String) = {
    val indentation = smallestIndentation(lines)
    val prefixPattern = Pattern.compile(s"^$indentation", Pattern.MULTILINE)
    prefixPattern.matcher(lines).replaceAll("")
  }

  private def smallestIndentation(lines: String): String = {
    val nonBlankLinesMatcher = Pattern.compile( """^(.*?)\S+?.*$""", Pattern.MULTILINE).matcher(lines)
    val indentationsOfNonBlankLines = mutable.Buffer[String]()
    while (nonBlankLinesMatcher.find()) {
      indentationsOfNonBlankLines += nonBlankLinesMatcher.group(1)
    }
    if (indentationsOfNonBlankLines.isEmpty) "" else indentationsOfNonBlankLines.minBy(_.length)
  }

  private def concatenateSnippetLines(snippetLines: Iterable[String], lineTransformer: String => String): String = {
    val strBuilder = new StringBuilder()
    for (line <- snippetLines) {
      if (strBuilder.nonEmpty) {
        strBuilder.append(LINE_SEPARATOR)
      }
      strBuilder.append(lineTransformer(line))
    }
    strBuilder.toString()
  }

  private def insertionErrorMessage(snippetName: String, sourceFile: String): String = {
    s"Could not insert the snippet '$snippetName' in file '$sourceFile'."
  }

  private def findSnippetFile(sourceFile: String): File = {
    snippetSearchPaths.toIterator
      .map(_ / sourceFile)
      .find(_.isFile)
      .getOrElse(file(sourceFile))
  }

}

object SnippetInserter {
  private val LINE_SEPARATOR = "\n"
  private val SNIPPET_START_PREFIX_PATTERN = "^.*?SNIPPET:\\s*?"
  private val SNIPPET_END_PREFIX_PATTERN = "^.*?ENDSNIPPET:\\s*?"
  private val snippetEndPrefixRegex = Pattern.compile(SNIPPET_END_PREFIX_PATTERN + ".*$")
  private val snippetStartPrefixRegex = Pattern.compile(SNIPPET_START_PREFIX_PATTERN + ".*$")

  def linesWithinSnippet(lines: Iterable[String], snippetName: String): Iterable[String] = {
    val snippetStartPattern = createSnippetStartPattern(snippetName)
    val snippetEndPattern = createSnippetEndPattern(snippetName)
    var insideSnippet = false
    lines.filter {
      line =>
        if (!insideSnippet) {
          insideSnippet = snippetStartPattern.matcher(line).matches()
          false
        } else {
          insideSnippet = !snippetEndPattern.matcher(line).matches()
          if (lineIsSnippetStartOrEndTag(line)) {
            false
          } else {
            insideSnippet
          }
        }
    }
  }

  private def createSnippetStartPattern(snippetName: String): Pattern = {
    Pattern.compile(SNIPPET_START_PREFIX_PATTERN + Pattern.quote(snippetName) + "$")
  }

  private def createSnippetEndPattern(snippetName: String): Pattern = {
    Pattern.compile(SNIPPET_END_PREFIX_PATTERN + Pattern.quote(snippetName) + "$")
  }

  private def lineIsSnippetStartOrEndTag(s: String): Boolean = {
    snippetEndPrefixRegex.matcher(s).matches() || snippetStartPrefixRegex.matcher(s).matches()
  }
}