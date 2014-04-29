package si.urbas.sbtutils.docs

import sbt._
import org.fusesource.scalate.{Binding, TemplateEngine}

private[docs] object DocsGenerator {

  private val SNIPPET_INSERTER_BINDING_NAME = "snippetInserter"

  def generateDocs(log: Logger,
                   projectBaseDir: File,
                   outputDirectory: File,
                   docsDirectories: Seq[File],
                   scratchDirectory: File,
                   docFiles: Seq[File],
                   snippetSearchPaths: Seq[File]): Seq[File] = {
    outputDirectory.mkdirs()
    val canonicalBaseDirs = docsDirectories.map(_.getCanonicalFile).toList
    val templateEngine = createTemplateEngine(canonicalBaseDirs, scratchDirectory)

    docFiles.map {
      docFile =>
        val relativeDocFile = findRelativePath(canonicalBaseDirs, docFile)
        val outputFile = file(toResolvedPathWithoutExtension(outputDirectory, relativeDocFile))
        log.info(s"Generating doc: ${docFile.getCanonicalPath} -> ${outputFile.getCanonicalPath}")
        val generatedDocContent = templateEngine.layout(relativeDocFile, createTemplateBindings(docFile, projectBaseDir +: snippetSearchPaths))
        IO.write(outputFile, generatedDocContent)
        outputFile
    }
  }

  private def createTemplateEngine(canonicalBaseDirs: List[File], scratchDirectory: File): TemplateEngine = {
    val templateEngine = new TemplateEngine(canonicalBaseDirs)
    templateEngine.escapeMarkup = false
    templateEngine.workingDirectory = scratchDirectory
    templateEngine.bindings = createTemplateBindingSpecs
    templateEngine
  }

  private def createTemplateBindingSpecs: List[Binding] = {
    List(Binding(SNIPPET_INSERTER_BINDING_NAME, classOf[SnippetInserter].getCanonicalName, importMembers = true))
  }

  private def findRelativePath(canonicalBaseDirs: List[File], docFile: File): String = {
    canonicalBaseDirs.view.map(base => base.relativize(docFile)).collectFirst {
      case Some(file) => file
    }.getOrElse(docFile).toString
  }

  private def createTemplateBindings(docFile: File, snippetSearchPaths: Seq[File]): Map[String, Any] = {
    Map(SNIPPET_INSERTER_BINDING_NAME -> new SnippetInserter(snippetSearchPaths :+ docFile.getParentFile))
  }

  private def toResolvedPathWithoutExtension(outputDirectory: File, relativeDocFile: String): String = {
    (outputDirectory / relativeDocFile).toString.replaceFirst("\\.[^.]+$", "")
  }

}
