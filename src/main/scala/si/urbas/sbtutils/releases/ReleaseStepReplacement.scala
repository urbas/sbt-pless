package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep

case class ReleaseStepReplacement(stepToReplace: ReleaseStep, withSteps: Seq[ReleaseStep]) extends ReleaseProcessTransformer {
  override def apply(releaseProcess: Seq[ReleaseStep]): Seq[ReleaseStep] = {
    val indexOfStepToReplace = releaseProcess.indexOf(stepToReplace)
    if (indexOfStepToReplace < 0) {
      throw new IllegalArgumentException("Could not replace a release step. The release step is not present in the release process.")
    }
    releaseProcess.flatMap {
      case x if x == stepToReplace =>
        withSteps
      case x =>
        Seq(x)
    }
  }
}
