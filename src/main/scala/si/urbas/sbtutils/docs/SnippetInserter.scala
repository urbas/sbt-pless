package si.urbas.sbtutils.docs

import sbt._
import java.util.regex.Pattern
import si.urbas.sbtutils.docs.SnippetInserter._
import java.io.FileNotFoundException

class SnippetInserter(projectBaseDir: File, templateFile: File) {

  def snippet(sourceFile: String, snippetName: String, linePrefix: String = ""): String = {
    val snippetFile = findSnippetFile(sourceFile)
    if (snippetFile.isFile) {
      val snippetLines = linesWithinSnippet(IO.readLines(snippetFile), snippetName)
      concatenateSnippetLines(snippetLines, linePrefix)
    } else {
      throw new FileNotFoundException(insertionErrorMessage(snippetName, sourceFile) + s" Could not find the file.")
    }
  }


  private def concatenateSnippetLines(snippetLines: Iterable[String], linePrefix: String): String = {
    val strBuilder = new StringBuilder()
    for (line <- snippetLines) {
      if (!strBuilder.isEmpty) {
        strBuilder.append(LINE_SEPARATOR)
      }
      strBuilder.append(linePrefix).append(line)
    }
    strBuilder.toString()
  }

  private def insertionErrorMessage(snippetName: String, sourceFile: String): String = {
    s"Could not insert the snippet '$snippetName' in file '$sourceFile'."
  }

  private def findSnippetFile(sourceFile: String): File = {
    List(projectBaseDir.getCanonicalFile, templateFile.getParentFile.getCanonicalFile)
      .map(_.relativize(file(sourceFile))).collectFirst {
      case Some(file) => file
    }.getOrElse(file(sourceFile))
  }

}

object SnippetInserter {
  private val LINE_SEPARATOR = "\n"

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
          insideSnippet
        }
    }
  }

  private def createSnippetStartPattern(snippetName: String): Pattern = {
    Pattern.compile("^.*?SNIPPET:\\s*?" + Pattern.quote(snippetName) + "$")
  }

  private def createSnippetEndPattern(snippetName: String): Pattern = {
    Pattern.compile("^.*?ENDSNIPPET:\\s*?" + Pattern.quote(snippetName) + "$")
  }
}