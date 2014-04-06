package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep

case class ReleaseStepInsertAfter(insertionPoint: ReleaseStep, stepsToInsert: Seq[ReleaseStep]) extends ReleaseStepInsert {
  override protected def insertionPredicate: ReleaseStep => Boolean = {
    IsStepAfterFilter(insertionPoint)
  }
}
