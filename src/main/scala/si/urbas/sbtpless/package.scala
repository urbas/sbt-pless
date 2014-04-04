package si.urbas

import sbt.State

package object sbtpless {
  type BuildFunction = State => State
}
