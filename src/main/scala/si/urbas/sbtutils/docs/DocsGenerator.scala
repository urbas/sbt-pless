package si.urbas.sbtutils.docs

import sbt._
import org.fusesource.scalate.{Binding, TemplateEngine}
import sbt.IO.utf8
import java.io.PrintWriter

private[docs] object DocsGenerator {

  private val SNIPPET_INSERTER_BINDING_NAME = "snippetInserter"

  def generateDocsImpl(logger: Logger,
                       projectBaseDir: File,
                       outputDirectory: File,
                       docsDirectories: Seq[File],
                       scratchDirectory: File,
                       docFileFilter: FileFilter,
                       snippetSearchPaths: Seq[File]): Seq[File] = {
    outputDirectory.mkdirs()

    val templateEngine = createTemplateEngine(docsDirectories, scratchDirectory)
    val docFiles = docsDirectories.flatMap(docsDir => PathFinder(docsDirectories).**(docFileFilter).get.map(_.relativeTo(docsDir).get))

    docFiles.map {
      docFile =>
        val outputFile = file(toOutputPathWithoutExtension(outputDirectory, docFile.getPath))
        logger.info(s"Generating doc: ${docFile.getCanonicalPath} -> ${outputFile.getCanonicalPath}")
        IO.writer(outputFile, "", utf8) {
          writer =>
            templateEngine.layout(docFile.getPath, new PrintWriter(writer), createTemplateBindings(docFile, projectBaseDir +: snippetSearchPaths))
        }
        outputFile
    }
  }

  private def createTemplateEngine(templateSourceDirs: Seq[File], scratchDirectory: File): TemplateEngine = {
    val canonicalSourceDirs = templateSourceDirs.map(_.getCanonicalFile).toList
    new TemplateEngine(canonicalSourceDirs) {
      escapeMarkup = false
      workingDirectory = scratchDirectory
      bindings = createTemplateBindingSpecs
    }
  }

  private def createTemplateBindingSpecs: List[Binding] = {
    List(Binding(SNIPPET_INSERTER_BINDING_NAME, classOf[SnippetInserter].getCanonicalName, importMembers = true))
  }

  private def createTemplateBindings(docFile: File, snippetSearchPaths: Seq[File]): Map[String, Any] = {
    Map(SNIPPET_INSERTER_BINDING_NAME -> new SnippetInserter(snippetSearchPaths :+ docFile.getParentFile))
  }

  private def toOutputPathWithoutExtension(outputDirectory: File, relativeDocFile: String): String = {
    (outputDirectory / relativeDocFile).toString.replaceFirst("\\.[^.]+$", "")
  }

}
