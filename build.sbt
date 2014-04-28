import com.typesafe.sbt.pgp.PgpKeys.publishSigned
import de.johoop.jacoco4sbt.JacocoPlugin._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys.releaseProcess
import sbtrelease.ReleaseStateTransformations._
import si.urbas.sbtutils.docs
// SNIPPET: importProcessTransformation
import si.urbas.sbtutils.releases.ReleaseProcessTransformation
// ENDSNIPPET: importProcessTransformation
import si.urbas.sbtutils.textfiles._
import si.urbas.sbtutils.textfiles.TextFileManipulation._
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
  "org.fusesource.scalate" %% "scalate-core" % "1.6.1",
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test",
  "org.slf4j" % "slf4j-simple" % "1.7.7"
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

// SNIPPET: generatingDocs
si.urbas.sbtutils.docs.tasks

docs.docsOutputDir := file(".")
// ENDSNIPPET: generatingDocs

si.urbas.sbtutils.textfiles.tasks

lazy val bumpVersionInPluginsSbtFile = taskKey[Unit]("Replaces any references to the version of this project in 'project/plugins.sbt'.")

bumpVersionInPluginsSbtFile := bumpVersionInFile(file("project/plugins.sbt"), organization.value, name.value, version.value)

// SNIPPET: releaseProcess
releaseProcess := ReleaseProcessTransformation
  .insertTasks(bumpVersionInReadmeMd, bumpVersionInPluginsSbtFile, addReadmeFileToVcs).after(setReleaseVersion)
  .replaceStep(publishArtifacts).withAggregatedTasks(publishSigned, sonatypeReleaseAll)
  .in(releaseProcess.value)
// ENDSNIPPET: releaseProcess
