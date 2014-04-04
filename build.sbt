import sbt._
import xerial.sbt.Sonatype
import xerial.sbt.Sonatype.SonatypeKeys
import sbtrelease.ReleasePlugin._

sbtPlugin := true

name := "sbt-pless"

organization := "si.urbas"

Sonatype.sonatypeSettings

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishMavenStyle := true

SonatypeKeys.profileName := "org.xerial"

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.3")

addSbtPlugin("com.typesafe.sbt" %% "sbt-pgp" % "0.8.3")

addSbtPlugin("org.xerial.sbt" %% "sbt-sonatype" % "0.2.1")

pomExtra := {
  <url>https://github.com/urbas/sbt-pless</url>
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:github.com/urbas/sbt-pless</connection>
      <developerConnection>scm:git:git@github.com:urbas/sbt-pless</developerConnection>
      <url>github.com/urbas/sbt-pless</url>
    </scm>
    <developers>
      <developer>
        <id>urbas</id>
        <name>urbas</name>
        <url>https://github.com/urbas</url>
      </developer>
    </developers>
}

releaseSettings
