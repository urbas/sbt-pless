package si.urbas.sbtutils.docs

import org.fusesource.scalate.Binding
import sbt._

trait TemplateBindingProvider {
  def bindingInfo: Binding
  def bindingInstance(docFile: File): Any
}
