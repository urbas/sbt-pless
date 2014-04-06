package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep
import sbt.TaskKey

trait ReleaseProcessTransformation {
  def insertReleaseSteps(steps: ReleaseStep*): ReleaseStepInsertions

  def insertGlobalTasks(tasks: TaskKey[_]*): ReleaseStepInsertions

  def replaceReleaseStep(stepToReplace: ReleaseStep): ReleaseStepReplacements
}

object ReleaseProcessTransformation {
  def insertReleaseSteps(steps: ReleaseStep*): ReleaseStepInsertions = {
    ReleaseStepInsertions(steps, Nil)
  }

  def insertGlobalTasks(tasks: TaskKey[_]*): ReleaseStepInsertions = {
    insertReleaseSteps(globalTasksToReleaseSteps(tasks): _*)
  }

  def replaceReleaseStep(stepToReplace: ReleaseStep): ReleaseStepReplacements = {
    ReleaseStepReplacements(stepToReplace, Nil)
  }

  def globalTasksToReleaseSteps(tasks: Seq[TaskKey[_]]): Seq[ReleaseStep] = {
    tasks.map(globalTaskToReleaseStep)
  }
}