package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep
import sbt.TaskKey

trait ReleaseProcessTransformation {
  def insertReleaseSteps(steps: ReleaseStep*): ReleaseStepInsertions

  def insertReleaseTasks(tasks: TaskKey[_]*): ReleaseStepInsertions

  def replaceReleaseStep(stepToReplace: ReleaseStep): ReleaseStepReplacements
}

object ReleaseProcessTransformation extends ReleaseProcessTransformation {
  override def insertReleaseSteps(steps: ReleaseStep*): ReleaseStepInsertions = {
    ReleaseStepInsertions(steps, Nil)
  }

  override def insertReleaseTasks(tasks: TaskKey[_]*): ReleaseStepInsertions = {
    insertReleaseSteps(globalTasksToReleaseSteps(tasks): _*)
  }

  override def replaceReleaseStep(stepToReplace: ReleaseStep): ReleaseStepReplacements = {
    ReleaseStepReplacements(stepToReplace, Nil)
  }
}