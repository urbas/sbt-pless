package si.urbas.sbtutils.docs

import sbt._
import org.fusesource.scalate.{Binding, TemplateEngine}
import sbt.IO.utf8
import java.io.PrintWriter

object DocsGenerator {

  def generateDocsImpl(logger: Logger,
                       projectBaseDir: File,
                       outputDirectory: File,
                       docsDirectories: Seq[File],
                       scratchDirectory: File,
                       docFileFilter: FileFilter,
                       snippetSearchPaths: Seq[File],
                       templateBindingProviders: Seq[TemplateBindingProvider]): Seq[File] = {
    outputDirectory.mkdirs()

    val templateEngine = createTemplateEngine(docsDirectories, scratchDirectory, templateBindingProviders)
    val docFiles = docsDirectories.flatMap(docsDir => PathFinder(docsDirectories).**(docFileFilter).get.map(_.relativeTo(docsDir).get))

    docFiles.map {
      docFile =>
        val outputFile = file(toOutputPathWithoutExtension(outputDirectory, docFile.getPath))
        logger.info(s"Generating doc: ${docFile.getCanonicalPath} -> ${outputFile.getCanonicalPath}")
        IO.writer(outputFile, "", utf8) {
          writer =>
            templateEngine.layout(docFile.getPath, new PrintWriter(writer), createBindingInstances(docFile, templateBindingProviders))
        }
        outputFile
    }
  }

  private def createTemplateEngine(templateSourceDirs: Seq[File],
                                   scratchDirectory: File,
                                   templateBindingProviders: Seq[TemplateBindingProvider]): TemplateEngine = {
    val canonicalSourceDirs = templateSourceDirs.map(_.getCanonicalFile).toList
    new TemplateEngine(canonicalSourceDirs) {
      escapeMarkup = false
      workingDirectory = scratchDirectory
      bindings = templateBindingProviders.map(_.bindingInfo).toList
    }
  }

  private def createBindingInstances(docFile: File, templateBindingProviders: Seq[TemplateBindingProvider]): Map[String, Any] = {
    templateBindingProviders.map(provider => (provider.bindingInfo.name, provider.bindingInstance(docFile))).toMap
  }

  private def toOutputPathWithoutExtension(outputDirectory: File, relativeDocFile: String): String = {
    (outputDirectory / relativeDocFile).toString.replaceFirst("\\.[^.]+$", "")
  }

}
