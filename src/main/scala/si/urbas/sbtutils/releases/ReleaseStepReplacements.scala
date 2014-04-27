package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep
import sbt.TaskKey

case class ReleaseStepReplacements(stepToReplace: ReleaseStep,
                                   previousTransforms: Seq[ReleaseProcessTransformer]) {

  def withReleaseSteps(steps: ReleaseStep*): ReleaseProcessTransformAggregate = {
    ReleaseProcessTransformAggregate(previousTransforms :+ ReleaseStepReplacement(stepToReplace, steps))
  }

  def withGlobalTasks(tasks: TaskKey[_]*): ReleaseProcessTransformAggregate = {
    ReleaseProcessTransformAggregate(previousTransforms :+ ReleaseStepReplacement(stepToReplace, globalTasksToReleaseSteps(tasks)))
  }

}
