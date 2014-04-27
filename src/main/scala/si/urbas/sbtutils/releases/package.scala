package si.urbas.sbtutils

import sbt._
import sbtrelease.ReleaseStep


package object releases {
  type ReleaseFunction = State => State

  def taskToReleaseStep(task: TaskKey[_]): ReleaseStep = {
    ReleaseStep({
      state =>
        val extractedProject = Project.extract(state)
        val projectRef = extractedProject.get(Keys.thisProjectRef)
        extractedProject.runTask(task in Global in projectRef, state)._1
    })
  }

  def aggregatedTaskToReleaseStep(task: TaskKey[_]): ReleaseStep = {
    ReleaseStep({
      state =>
        val extractedProject = Project.extract(state)
        val projectRef = extractedProject.get(Keys.thisProjectRef)
        extractedProject.runAggregated(task in Global in projectRef, state)
    })
  }

  def tasksToReleaseSteps(tasks: Seq[TaskKey[_]]): Seq[ReleaseStep] = {
    tasks.map(taskToReleaseStep)
  }
}
