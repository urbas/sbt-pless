package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep

case class ReleaseStepRemover(stepsToRemove: Set[ReleaseStep]) extends ReleaseProcessTransformer {
  override def apply(releaseProcess: Seq[ReleaseStep]): Seq[ReleaseStep] = {
    releaseProcess.filterNot(stepsToRemove.contains)
  }
}
