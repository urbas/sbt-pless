package si.urbas.sbtutils

import sbt._
import sbt.Keys._
import si.urbas.sbtutils.vcs._

package object docs {

  import si.urbas.sbtutils.docs.DocsGenerator.generateDocsImpl

  lazy val sspDocsDir = settingKey[File]("Default SSP docs directory.")
  lazy val docsOutputDir = settingKey[File]("the directory into which output docs will be placed.")
  lazy val docsDirs = settingKey[Seq[File]]("a list of SSP directories under which SSP files should be looked up.")
  lazy val docsSnippetDirs = settingKey[Seq[File]]("a list of directories where to look for snippet files.")
  lazy val generateDocs = taskKey[Seq[File]]("Generates documentation by processing templates and outputting the result into the configured output directory.")
  lazy val generateAndStageDocs = taskKey[Unit]("Generates documentation files and stages them in the VCS.")

  lazy val tasks = Seq[Def.Setting[_]](
    sspDocsDir := sourceDirectory.value / "main" / DOCS_SOURCES_DIR,
    docsDirs := Seq(sspDocsDir.value),
    docsOutputDir := target.value / DOCS_OUTPUT_DIR,
    includeFilter in generateDocs := "*.ssp",
    docsSnippetDirs := (sourceDirectories in Compile).value ++ docsDirs.value ++ (sourceDirectories in Test).value,
    generateDocs := generateDocsImpl(streams.value.log, baseDirectory.value, docsOutputDir.value, docsDirs.value, target.value / DOCS_SCRATCH_DIR, (includeFilter in generateDocs).value -- (excludeFilter in generateDocs).value, docsSnippetDirs.value),
    generateAndStageDocs := generateDocs.value.foreach(addFileToVcsImpl(state.value, _))
  )

  private val DOCS_SCRATCH_DIR = "docs-scratch"
  private val DOCS_SOURCES_DIR = "docs"
  private val DOCS_OUTPUT_DIR = "docs"

}