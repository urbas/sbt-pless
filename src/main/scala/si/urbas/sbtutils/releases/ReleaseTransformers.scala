package si.urbas.sbtutils.releases

import sbtrelease.{releaseTask, ReleaseStep}
import sbt.TaskKey
import si.urbas.sbtutils.releases.ReleaseTransformers.tasks2buildSteps

object ReleaseTransformers {
  def insertReleaseSteps(steps: ReleaseStep*): ReleaseStepInsertion = {
    ReleaseStepInsertion(steps: _*)
  }

  def insertTasks(tasks: TaskKey[_]*): ReleaseStepInsertion = {
    ReleaseStepInsertion(tasks2buildSteps(tasks): _*)
  }

  def replaceReleaseStep(stepToReplace: ReleaseStep): ReleaseStepReplacement = {
    ReleaseStepReplacement(stepToReplace)
  }

  def tasks2buildSteps(tasks: Seq[TaskKey[_]]): Seq[ReleaseStep] = {
    tasks.map(releaseTask(_)).map(ReleaseStep(_))
  }
}

case class ReleaseStepReplacement(stepToReplace: ReleaseStep) {
  def withReleaseSteps(steps: ReleaseStep*): ReleaseStepReplacementWithNewStep = {
    ReleaseStepReplacementWithNewStep(stepToReplace, steps: _*)
  }

  def withTasks(tasks: TaskKey[_]*): ReleaseStepReplacementWithNewStep = {
    ReleaseStepReplacementWithNewStep(stepToReplace, tasks2buildSteps(tasks): _*)
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