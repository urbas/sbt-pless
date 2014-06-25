package si.urbas.sbtutils.docs

import org.fusesource.scalate.Binding
import SnippetInserterTemplateBinding._
import sbt._

case class SnippetInserterTemplateBinding(snippetSearchPaths: Seq[File]) extends TemplateBinding {

  val bindingInfo: Binding = {
    Binding(SNIPPET_INSERTER_BINDING_NAME, classOf[SnippetInserter].getCanonicalName, importMembers = true)
  }

  def bindingInstance(docFile: File): Any = {
    new SnippetInserter(snippetSearchPaths :+ docFile.getParentFile)
  }
}

object SnippetInserterTemplateBinding {
  val SNIPPET_INSERTER_BINDING_NAME = "snippetInserter"
}