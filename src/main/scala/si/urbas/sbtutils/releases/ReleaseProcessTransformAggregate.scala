package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep
import sbt.TaskKey

case class ReleaseProcessTransformAggregate(transformations: Seq[ReleaseProcessTransformer]) extends ReleaseProcessTransformation {
  def in(releaseProcess: Seq[ReleaseStep]): Seq[ReleaseStep] = {
    var newReleaseProcess = releaseProcess
    for (transform <- transformations) {
      newReleaseProcess = transform(newReleaseProcess)
    }
    newReleaseProcess
  }

  override def insertReleaseSteps(steps: ReleaseStep*): ReleaseStepInsertions = {
    ReleaseStepInsertions(steps, transformations)
  }

  override def insertReleaseTasks(tasks: TaskKey[_]*): ReleaseStepInsertions = {
    ReleaseStepInsertions(globalTasksToReleaseSteps(tasks), transformations)
  }

  override def replaceReleaseStep(stepToReplace: ReleaseStep): ReleaseStepReplacements = {
    ReleaseStepReplacements(stepToReplace, transformations)
  }
}
