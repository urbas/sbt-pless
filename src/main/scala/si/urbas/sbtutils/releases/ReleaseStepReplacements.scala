package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep
import sbt.TaskKey

case class ReleaseStepReplacements(stepToReplace: ReleaseStep,
                                   previousTransforms: Seq[ReleaseProcessTransformer]) {

  def withSteps(steps: ReleaseStep*): ReleaseProcessTransformAggregate = {
    ReleaseProcessTransformAggregate(previousTransforms :+ ReleaseStepReplacement(stepToReplace, steps))
  }

  def withTasks(tasks: TaskKey[_]*): ReleaseProcessTransformAggregate = {
    withSteps(tasksToReleaseSteps(tasks):_*)
  }

  def withAggregatedTasks(tasks: TaskKey[_]*): ReleaseProcessTransformAggregate = {
    withSteps(aggregatedTasksToReleaseSteps(tasks):_*)
  }

}
