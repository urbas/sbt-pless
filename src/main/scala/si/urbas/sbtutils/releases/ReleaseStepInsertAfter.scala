package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep

case class ReleaseStepInsertAfter(insertionPoint: ReleaseStep,
                                  stepsToInsert: Seq[ReleaseStep])
  extends ReleaseStepInsert {

  override protected val insertionPredicate: ReleaseStep => Boolean = {
    IsStepAfterFilter(insertionPoint)
  }
}
