package si.urbas.sbtutils.docs

import sbt._
import java.util.regex.Pattern

class SnippetInserter(projectBaseDir: File, currentTemplate: File) {
  def snippet(sourceFile: String, snippetName: String, linePrefix: String): String = {
    val fileOpt = List(projectBaseDir.getCanonicalFile, currentTemplate.getParentFile.getCanonicalFile)
      .map(_.relativize(file(sourceFile))).collectFirst {
      case Some(file) => file
    }
    val tehFile = fileOpt.getOrElse(file(sourceFile))
    if (tehFile.isFile) {
      val strBuilder = new StringBuilder()
      var insideSnippet = false
      for (line <- IO.readLines(tehFile)) {
        if (!insideSnippet) {
          insideSnippet = line.matches("^.*?SNIPPET:\\s*?" + Pattern.quote(snippetName) + "$")
        } else {
          insideSnippet = !line.matches("^.*?ENDSNIPPET:\\s*?" + Pattern.quote(snippetName) + "$")
          if (insideSnippet) {
            if (!strBuilder.isEmpty) {
              strBuilder.append(System.lineSeparator())
            }
            strBuilder.append(linePrefix).append(line)
          }
        }
      }
      strBuilder.toString()
    } else {
      throw new RuntimeException(s"Sad times... cannot find $sourceFile")
    }
  }
}
