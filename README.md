# sbt-pless

[![Build Status](https://travis-ci.org/urbas/sbt-pless.png?branch=master)](https://travis-ci.org/urbas/sbt-pless)

## Usage

1.  Add the following lines to the `project/plugins.sbt` file:

        resolvers += "Sonatype Public Repository" at "https://oss.sonatype.org/content/groups/public"

        addSbtPlugin("si.urbas" % "sbt-pless" % "0.0.5")

2.  Add this line at the top of `build.sbt`:

    ```scala
    import si.urbas.sbtutils.releases.ReleaseProcessTransformation
    ```

3.  Now you can transform your release process like this (this example uses the `sbtrelease` and the `pgpkeys` plugins):

    ```scala
    si.urbas.sbtutils.textfiles.tasks
    
    lazy val bumpVersionInPluginsSbtFile = taskKey[Unit]("Replaces any references to the version of this project in 'project/plugins.sbt'.")
    
    bumpVersionInPluginsSbtFile := bumpVersionInFile(file("project/plugins.sbt"), organization.value, name.value, version.value)
    
    releaseProcess := ReleaseProcessTransformation
      .insertTasks(bumpVersionInReadmeMd, bumpVersionInPluginsSbtFile, addReadmeFileToVcs).after(setReleaseVersion)
      .replaceStep(publishArtifacts).withAggregatedTasks(publishSigned, sonatypeReleaseAll)
      .in(releaseProcess.value)
    ```
