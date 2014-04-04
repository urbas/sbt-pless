package si.urbas.sbtpless

import sbtrelease.ReleaseStep

object ReleaseTransformers {
  def insertReleaseSteps(steps: ReleaseStep*): ReleaseStepInsertion = {
    ReleaseStepInsertion(steps:_*)
  }

  def replaceReleaseStep(stepToReplace: ReleaseStep): ReleaseStepReplacement = {
    ReleaseStepReplacement(stepToReplace)
  }
}

case class ReleaseStepReplacement(stepToReplace: ReleaseStep) {
  def withReleaseSteps(steps: ReleaseStep*): ReleaseStepReplacementWithNewStep = {
    ReleaseStepReplacementWithNewStep(stepToReplace, steps:_*)
  }
}

case class ReleaseStepReplacementWithNewStep(stepToReplace: ReleaseStep, withSteps: ReleaseStep*) {
  def in(releaseProcess: Seq[ReleaseStep]): Seq[ReleaseStep] = {
    releaseProcess.flatMap {
      case x if x == stepToReplace =>
        withSteps
      case x =>
        Seq(x)
    }
  }
}

case class ReleaseStepInsertion(steps: ReleaseStep*) {
  def into(releaseProcess: Seq[ReleaseStep]): ReleaseStepInsertionWithReleaseProcess = {
    ReleaseStepInsertionWithReleaseProcess(this, releaseProcess)
  }
}

case class ReleaseStepInsertionWithReleaseProcess(insertion: ReleaseStepInsertion, releaseProcess: Seq[ReleaseStep]) {
  def before(step: ReleaseStep): Seq[ReleaseStep] = {
    val (stepsBefore, stepsAfter) = releaseProcess.partition(IsStepBeforeFilter(step))
    stepsBefore ++ insertion.steps ++ stepsAfter
  }
}

case class IsStepBeforeFilter(targetStep: ReleaseStep) extends (ReleaseStep => Boolean) {
  var wasSetVersionStepFound = false

  override def apply(step: ReleaseStep): Boolean = {
    if (!wasSetVersionStepFound) {
      wasSetVersionStepFound = step == targetStep
    }
    !wasSetVersionStepFound
  }
}