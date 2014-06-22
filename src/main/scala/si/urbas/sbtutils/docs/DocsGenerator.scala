package si.urbas.sbtutils.docs

import java.io.PrintWriter

import org.fusesource.scalate.TemplateEngine
import sbt.IO.utf8
import sbt.Keys._
import sbt._

object DocsGenerator {

  def generateDocsTaskImpl: Def.Initialize[Task[Seq[File]]] = Def.task {
    val logger = streams.value.log
    val outputDirectory = docsOutputDir.value
    val docsDirectories = docsDirs.value
    val scratchDirectory = target.value / DOCS_SCRATCH_DIR
    val docFileFilter = (includeFilter in generateDocs).value -- (excludeFilter in generateDocs).value
    val snippetSearchPaths = docsSnippetDirs.value
    val templateBindingProviders = docTemplateBindings.value

    outputDirectory.mkdirs()

    val templateEngine = createTemplateEngine(docsDirectories, scratchDirectory, templateBindingProviders, docsCompilerClasspath.value, docsClassLoader.value)
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
                                   templateBindingProviders: Seq[TemplateBindingProvider],
                                   extraClasspath: String,
                                   customClassLoader: Option[ClassLoader]): TemplateEngine = {
    val canonicalSourceDirs = templateSourceDirs.map(_.getCanonicalFile).toList
    new TemplateEngine(canonicalSourceDirs) {
      escapeMarkup = false
      workingDirectory = scratchDirectory
      bindings = templateBindingProviders.map(_.bindingInfo).toList
      combinedClassPath = true
      classpath = extraClasspath
      customClassLoader.foreach{
        _classLoader =>
          classLoader = _classLoader
      }
    }
  }

  private def createBindingInstances(docFile: File, templateBindingProviders: Seq[TemplateBindingProvider]): Map[String, Any] = {
    templateBindingProviders.map(provider => (provider.bindingInfo.name, provider.bindingInstance(docFile))).toMap
  }

  private def toOutputPathWithoutExtension(outputDirectory: File, relativeDocFile: String): String = {
    (outputDirectory / relativeDocFile).toString.replaceFirst("\\.[^.]+$", "")
  }

}
