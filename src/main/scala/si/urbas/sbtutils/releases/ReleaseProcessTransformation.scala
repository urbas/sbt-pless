package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep
import sbt.TaskKey

trait ReleaseProcessTransformation {

  def insertReleaseSteps(steps: ReleaseStep*): ReleaseStepInsertions = {
    ReleaseStepInsertions(steps, previousTransformations)
  }

  def insertReleaseTasks(tasks: TaskKey[_]*): ReleaseStepInsertions = {
    ReleaseStepInsertions(globalTasksToReleaseSteps(tasks), previousTransformations)
  }

  def removeReleaseSteps(steps: ReleaseStep*): ReleaseProcessTransformAggregate = {
    ReleaseProcessTransformAggregate(ReleaseStepRemover(steps.toSet) +: previousTransformations)
  }

  def replaceReleaseStep(stepToReplace: ReleaseStep): ReleaseStepReplacements = {
    ReleaseStepReplacements(stepToReplace, previousTransformations)
  }

  protected def previousTransformations: Seq[ReleaseProcessTransformer] = {
    Nil
  }
}

object ReleaseProcessTransformation extends ReleaseProcessTransformation