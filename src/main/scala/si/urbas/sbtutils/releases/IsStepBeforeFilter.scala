package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep

case class IsStepBeforeFilter(targetStep: ReleaseStep) extends (ReleaseStep => Boolean) {
  var wasSetVersionStepFound = false

  override def apply(step: ReleaseStep): Boolean = {
    if (!wasSetVersionStepFound) {
      wasSetVersionStepFound = step == targetStep
    }
    !wasSetVersionStepFound
  }
}
