package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep

case class IsStepAfterFilter(targetStep: ReleaseStep) extends (ReleaseStep => Boolean) {
  var wasSetVersionStepFound = false

  override def apply(step: ReleaseStep): Boolean = {
    val oldFoundFlag = wasSetVersionStepFound
    if (!wasSetVersionStepFound) {
      wasSetVersionStepFound = step == targetStep
    }
    !oldFoundFlag
  }
}
