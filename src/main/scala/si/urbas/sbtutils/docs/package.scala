package si.urbas.sbtutils

import sbt._
import sbt.Keys._
import sbt.Defaults.collectFiles

package object docs {

  import si.urbas.sbtutils.docs.DocsGenerator.generateDocs

  lazy val sspDocsDir = settingKey[File]("Default SSP docs directory.")
  lazy val docsOutputDir = settingKey[File]("the directory into which output docs will be placed.")
  lazy val docsDirs = settingKey[Seq[File]]("a list of SSP directories under which SSP files should be looked up.")
  lazy val docs = taskKey[Seq[File]]("a list of SSP files that should be processed.")
  lazy val generateSspDocs = taskKey[Seq[File]]("processes Scalate SSP template files")

  lazy val tasks = Seq[Def.Setting[_]](
    sspDocsDir := sourceDirectory.value / "main" / DOCS_SOURCES_DIR,
    docsDirs := Seq(sspDocsDir.value),
    docsOutputDir := target.value / DOCS_OUTPUT_DIR,
    includeFilter in docs := "*.ssp",
    docs <<= collectFiles(docsDirs, includeFilter in docs, excludeFilter in docs),
    generateSspDocs := generateDocs(state.value.log, baseDirectory.value, docsOutputDir.value, docsDirs.value, target.value / DOCS_SCRATCH_DIR, docs.value, (sourceDirectories in Compile).value)
  )

  private val DOCS_SCRATCH_DIR = "docs-scratch"
  private val DOCS_SOURCES_DIR = "docs"
  private val DOCS_OUTPUT_DIR = "docs"

}