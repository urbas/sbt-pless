package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep

trait ReleaseStepInsert extends ReleaseProcessTransformer {
  def apply(releaseProcess: Seq[ReleaseStep]): Seq[ReleaseStep] = {
    val (stepsBefore, stepsAfter) = releaseProcess.partition(insertionPredicate)
    stepsBefore ++ stepsToInsert ++ stepsAfter
  }

  protected val stepsToInsert: Seq[ReleaseStep]

  protected def insertionPredicate: ReleaseStep => Boolean
}
