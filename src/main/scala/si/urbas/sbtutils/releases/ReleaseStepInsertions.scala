package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep

case class ReleaseStepInsertions(steps: Seq[ReleaseStep], previousTransforms: Seq[ReleaseProcessTransformer]) {

  def after(step: ReleaseStep): ReleaseProcessTransformAggregate = {
    ReleaseProcessTransformAggregate(previousTransforms :+ ReleaseStepInsertAfter(step, steps))
  }

  def before(step: ReleaseStep): ReleaseProcessTransformAggregate = {
    ReleaseProcessTransformAggregate(previousTransforms :+ ReleaseStepInsertBefore(step, steps))
  }
}
