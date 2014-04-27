package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep

case class ReleaseProcessTransformAggregate(override val previousTransformations: Seq[ReleaseProcessTransformer])
  extends ReleaseProcessTransformation {

  def in(releaseProcess: Seq[ReleaseStep]): Seq[ReleaseStep] = {
    var newReleaseProcess = releaseProcess
    for (transform <- previousTransformations) {
      newReleaseProcess = transform(newReleaseProcess)
    }
    newReleaseProcess
  }
}
