import sbt._

sbtPlugin := true

name := "sbt-pless"

organization := "si.urbas"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.3")

addSbtPlugin("com.typesafe.sbt" %% "sbt-pgp" % "0.8.3")

addSbtPlugin("org.xerial.sbt" %% "sbt-sonatype" % "0.2.1")