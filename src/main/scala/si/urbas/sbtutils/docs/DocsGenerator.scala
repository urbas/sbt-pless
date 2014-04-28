package si.urbas.sbtutils.docs

import sbt._
import org.fusesource.scalate.{Binding, TemplateEngine}

private[docs] object DocsGenerator {

  private val SNIPPET_INSERTER_BINDING_NAME = "snippetInserter"

  def generateDocs(log: Logger,
                   outputDirectory: File,
                   docsDirectories: Seq[File],
                   scratchDirectory: File,
                   docFiles: Seq[File]) {
    log.info(s"Processing documentation...")
    outputDirectory.mkdirs()
    val canonicalBaseDirs = docsDirectories.map(_.getCanonicalFile).toList
    val templateEngine = createTemplateEngine(canonicalBaseDirs, scratchDirectory)

    for (docFile <- docFiles) {
      val relativeDocFile = findRelativePath(canonicalBaseDirs, docFile)
      val outputFile = toResolvedPathWithoutExtension(outputDirectory, relativeDocFile)
      log.info(s"Generating doc: $relativeDocFile -> ${outputDirectory.relativize(file(outputFile)).get}")
      val generatedDocContent = templateEngine.layout(relativeDocFile, createTemplateBindings(docFile))
      IO.write(file(outputFile), generatedDocContent)
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

  private def createTemplateBindings(docFile: File): Map[String, Any] = {
    Map(SNIPPET_INSERTER_BINDING_NAME -> new SnippetInserter(file("."), docFile))
  }

  private def toResolvedPathWithoutExtension(outputDirectory: File, relativeDocFile: String): String = {
    (outputDirectory / relativeDocFile).toString.replaceFirst("\\.[^.]+$", "")
  }

}
