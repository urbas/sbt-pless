package si.urbas.sbtutils

import sbt.Keys._
import sbt._
import si.urbas.sbtutils.vcs._

package object docs {

  val DOCS_SCRATCH_DIR = "docs-scratch"
  val DOCS_SOURCES_DIR = "docs"
  val DOCS_OUTPUT_DIR = "docs"

  lazy val sspDocsDir = settingKey[File]("Default SSP docs directory.")
  lazy val docsOutputDir = settingKey[File]("the directory into which output docs will be placed.")
  lazy val docTemplateBindings = settingKey[Seq[TemplateBindingProvider]]("these provide Scala objects that will be inserted into documentation templates.")
  lazy val docsDirs = settingKey[Seq[File]]("a list of SSP directories under which SSP files should be looked up.")
  lazy val docsSnippetDirs = settingKey[Seq[File]]("a list of directories where to look for snippet files.")
  lazy val generateDocs = taskKey[Seq[File]]("Generates documentation by processing templates and outputting the result into the configured output directory.")
  lazy val generateAndStageDocs = taskKey[Unit]("Generates documentation files and stages them in the VCS.")
  lazy val docsCompilerClasspath = settingKey[String]("this classpath is added to the docs template compiler.")
  lazy val docsClassLoader = settingKey[Option[ClassLoader]]("this classloader will be used when executing the templates.")

  lazy val tasks = Seq[Def.Setting[_]](
    docsCompilerClasspath := {
      (baseDirectory.value / "project" / "target" * "scala-*" / s"sbt-${sbtBinaryVersion.value}" / "classes")
        .get
        .map(_.getCanonicalPath)
        .mkString(java.io.File.pathSeparator)
    },
    docsClassLoader := None,
    sspDocsDir := sourceDirectory.value / "main" / DOCS_SOURCES_DIR,
    docsDirs := Seq(sspDocsDir.value),
    docTemplateBindings := List(SnippetInserterTemplateBinding(baseDirectory.value +: docsSnippetDirs.value)),
    docsOutputDir := baseDirectory.value,
    includeFilter in generateDocs := "*.ssp",
    docsSnippetDirs := (sourceDirectories in Compile).value ++ docsDirs.value ++ (sourceDirectories in Test).value,
    generateDocs <<= DocsGenerator.generateDocsTaskImpl,
    generateAndStageDocs := generateDocs.value.foreach(addFileToVcsImpl(state.value, _))
  )

}