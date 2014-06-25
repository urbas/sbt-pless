package si.urbas.sbtutils.docs

import org.fusesource.scalate.Binding
import sbt._

import scala.reflect.{classTag, ClassTag}

trait TemplateBinding {
  def bindingInfo: Binding
  def bindingInstance(docFile: File): Any
}

object TemplateBinding {
  def apply(binding: Binding, instance: Any): TemplateBinding = {
    new TemplateBinding {
      override val bindingInfo: Binding = binding

      override def bindingInstance(docFile: File): Any = instance
    }
  }

  def apply(binding: Binding, instanceCreator: File => Any): TemplateBinding = {
    new TemplateBinding {
      override val bindingInfo: Binding = binding

      override def bindingInstance(docFile: File): Any = instanceCreator(docFile)
    }
  }

  def apply[T : ClassTag](bindingName: String,
                          instance: T,
                          importMembers: Boolean = true): TemplateBinding = {
    new TemplateBinding {
      override val bindingInfo: Binding = Binding(
        bindingName,
        classTag[T].runtimeClass.getCanonicalName,
        importMembers
      )

      override def bindingInstance(docFile: File): Any = instance
    }
  }
}