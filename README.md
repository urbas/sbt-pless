# sbt-pless

[![Build Status](https://travis-ci.org/urbas/sbt-pless.png?branch=master)](https://travis-ci.org/urbas/sbt-pless)

## Usage

1.  Add the following line to the `project/plugins.sbt` file:

        addSbtPlugin("si.urbas" % "sbt-pless" % "0.0.1-SNAPSHOT")

2.  Add this line at the top of `build.sbt`:

        import si.urbas.sbtutils.releases.ReleaseProcessTransformation._

3.  Now you can transform your release process like this (this example uses the `sbtrelease` and the `pgpkeys` plugins):

        si.urbas.sbtutils.textfiles.tasks

        releaseProcess := insertGlobalTasks(bumpVersionInReadmeMd).before(setReleaseVersion)
          .replaceReleaseStep(publishArtifacts).withGlobalTasks(publishSigned, sonatypeReleaseAll)
          .in(releaseProcess.value)