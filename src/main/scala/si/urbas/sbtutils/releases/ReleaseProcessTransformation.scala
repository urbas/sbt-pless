package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep
import sbt.TaskKey

trait ReleaseProcessTransformation {

  def insertSteps(steps: ReleaseStep*): ReleaseStepInsertions = {
    ReleaseStepInsertions(steps, previousTransformations)
  }

  def insertTasks(tasks: TaskKey[_]*): ReleaseStepInsertions = {
    insertSteps(tasksToReleaseSteps(tasks):_*)
  }

  def insertAggregatedTasks(tasks: TaskKey[_]*): ReleaseStepInsertions = {
    insertSteps(aggregatedTasksToReleaseSteps(tasks):_*)
  }

  def removeSteps(steps: ReleaseStep*): ReleaseProcessTransformAggregate = {
    ReleaseProcessTransformAggregate(previousTransformations :+ ReleaseStepRemover(steps.toSet))
  }

  def replaceStep(stepToReplace: ReleaseStep): ReleaseStepReplacements = {
    ReleaseStepReplacements(stepToReplace, previousTransformations)
  }

  protected def previousTransformations: Seq[ReleaseProcessTransformer] = {
    Nil
  }
}

object ReleaseProcessTransformation extends ReleaseProcessTransformation