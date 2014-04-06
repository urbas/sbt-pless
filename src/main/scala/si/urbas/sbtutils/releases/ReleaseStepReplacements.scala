package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep
import sbt.TaskKey
import si.urbas.sbtutils.releases.ReleaseProcessTransformation.globalTasksToReleaseSteps

case class ReleaseStepReplacements(stepToReplace: ReleaseStep, previousTransforms: Seq[ReleaseProcessTransformer]) {
  def withReleaseSteps(steps: ReleaseStep*) = {
    ReleaseProcessTransformAggregate(previousTransforms :+ ReleaseStepReplacement(stepToReplace, steps))
  }

  def withGlobalTasks(tasks: TaskKey[_]*) = {
    ReleaseProcessTransformAggregate(previousTransforms :+ ReleaseStepReplacement(stepToReplace, globalTasksToReleaseSteps(tasks)))
  }
}
