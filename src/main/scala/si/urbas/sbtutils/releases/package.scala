package si.urbas.sbtutils

import sbt._


package object releases {
  type ReleaseFunction = State => State
}
