package si.urbas.sbtutils.releases

import sbtrelease.ReleaseStep

trait ReleaseProcessTransformer {
  def apply(releaseProcess: Seq[ReleaseStep]): Seq[ReleaseStep]
}
