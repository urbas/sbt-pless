package si.urbas.sbtutils

import sbt._
import sbt.Keys._
import sbt.Defaults.collectFiles
import org.fusesource.scalate.{Binding, TemplateEngine}

package object docs {

  lazy val sspDocsDir = settingKey[File]("Default SSP docs directory.")
  lazy val docsOutputDir = settingKey[File]("the directory into which output docs will be placed.")
  lazy val docsDirs = settingKey[Seq[File]]("a list of SSP directories under which SSP files should be looked up.")
  lazy val docs = taskKey[Seq[File]]("a list of SSP files that should be processed.")
  lazy val generateSspDocs = taskKey[Unit]("processes Scalate SSP template files")

  lazy val tasks = {
    Seq[Def.Setting[_]](
      sspDocsDir := sourceDirectory.value / "main" / "docs",
      docsDirs := Seq(sspDocsDir.value),
      docsOutputDir := target.value / "docs",
      includeFilter in docs := "*.ssp",
      docs <<= collectFiles(docsDirs, includeFilter in docs, excludeFilter in docs),
      generateSspDocs := generateDocs(
        state.value.log,
        docsOutputDir.value,
        docsDirs.value,
        target.value / "docs-scratch",
        docs.value
      )
    )
  }


  private def generateDocs(log: Logger, outputDirectory: File, docsDirectories: Seq[File], scratchDirectory: File, docFiles: Seq[File]) {
    log.info(s"Processing documentation...")
    outputDirectory.mkdirs()
    val canonicalBases = docsDirectories.map(_.getCanonicalFile).toList
    val templateEngine = new TemplateEngine(canonicalBases)
    templateEngine.escapeMarkup = false
    templateEngine.workingDirectory = scratchDirectory
    templateEngine.bindings = List(Binding("snippetInserter", classOf[SnippetInserter].getCanonicalName, importMembers = true))

    for (docFile <- docFiles) {
      val relativeDocFile = canonicalBases.view.map(base => base.relativize(docFile)).collectFirst {
        case Some(file) => file
      }.getOrElse(docFile).toString
      val outputFile = (outputDirectory / relativeDocFile).toString.replaceFirst("\\.[^.]+$", "")
      log.info(s"Generating doc: $relativeDocFile -> ${outputDirectory.relativize(file(outputFile)).get}")
      val content = templateEngine.layout(relativeDocFile, Map("snippetInserter" -> new SnippetInserter(file("."), docFile)))
      IO.write(file(outputFile), content)
    }
  }
}
