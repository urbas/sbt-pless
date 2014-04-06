package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep

trait ReleaseStepInsert extends ReleaseProcessTransformer {
  def apply(releaseProcess: Seq[ReleaseStep]): Seq[ReleaseStep] = {
    val (stepsBefore, stepsAfter) = releaseProcess.partition(insertionPredicate)
    assertInsertionNotOnBoundaries(stepsBefore, stepsAfter)
    stepsBefore ++ stepsToInsert ++ stepsAfter
  }


  protected def assertInsertionNotOnBoundaries(stepsBefore: Seq[ReleaseStep], stepsAfter: Seq[ReleaseStep]) {
    if (stepsAfter.isEmpty) {
      throw new IllegalArgumentException(s"Trying to 'insert' release steps at the end of the release process. " +
        s"If this is intended, please use the append operator instead.")
    }
    if (stepsBefore.isEmpty) {
      throw new IllegalArgumentException(s"Trying to 'insert' release steps to the front of the release process. " +
        s"If this is intended, please use the prepend operator instead.")
    }
  }

  protected val stepsToInsert: Seq[ReleaseStep]

  protected def insertionPredicate: ReleaseStep => Boolean
}
