import com.typesafe.sbt.pgp.PgpKeys.publishSigned
import de.johoop.jacoco4sbt.JacocoPlugin._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys.releaseProcess
import sbtrelease.ReleaseStateTransformations._
import si.urbas.sbtutils.releases.ReleaseProcessTransformation._
import si.urbas.sbtutils.textfiles._
import xerial.sbt.Sonatype
import xerial.sbt.Sonatype.SonatypeKeys
import sbt._
import xerial.sbt.Sonatype.SonatypeKeys.sonatypeReleaseAll

sbtPlugin := true

name := "sbt-pless"

organization := "si.urbas"

Sonatype.sonatypeSettings

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishMavenStyle := true

SonatypeKeys.profileName := "org.xerial"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

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

sources in doc in Compile := Nil

publishArtifact in(Compile, packageDoc) := true

publishArtifact in(Compile, packageSrc) := true

publishArtifact in(Test, packageBin) := true

publishArtifact in(Test, packageSrc) := true

jacoco.settings

releaseSettings

si.urbas.sbtutils.textfiles.tasks

releaseProcess := insertGlobalTasks(bumpVersionInReadmeMd).before(setReleaseVersion)
  .replaceReleaseStep(publishArtifacts).withGlobalTasks(publishSigned, sonatypeReleaseAll)
  .in(releaseProcess.value)