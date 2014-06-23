# sbt-pless

[![Build Status](https://travis-ci.org/urbas/sbt-pless.png?branch=master)](https://travis-ci.org/urbas/sbt-pless)

## Usage

Add the following lines to the `project/plugins.sbt` file:

```scala
resolvers += "Sonatype Public Repository" at "https://oss.sonatype.org/content/groups/public"

addSbtPlugin("si.urbas" % "sbt-pless" % "0.0.9")
```

### Release process transformations

Add this line at the top of `build.sbt`:

```scala
import si.urbas.sbtutils.releases.ReleaseProcessTransformation
```

Now you can transform your release process like this (this example uses the `sbtrelease` and the `pgpkeys` plugins):

```scala
releaseProcess := ReleaseProcessTransformation
  .insertTasks(bumpVersionInReadmeMd, generateAndStageDocs, bumpVersionInPluginsSbtFile, addReadmeFileToVcs).after(setReleaseVersion)
  .replaceStep(publishArtifacts).withAggregatedTasks(publishSigned, sonatypeReleaseAll)
  .in(releaseProcess.value)
```

### Documentation generation

Add the following to your `build.sbt` file:

```scala
si.urbas.sbtutils.docs.tasks
```

Now create the file `src/main/docs/README.md.ssp` and run the following SBT task:

generateDocs

This will generate the `README.md` file in the project's base directory. If you want the documentation to be output
elsewhere, add the following to your `build.sbt` file and change `baseDirectory.value` with your desired setting:

```scala
docsOutputDir := baseDirectory.value
```

See [Scalate's SSP documentation](http://scalate.fusesource.org/documentation/ssp-reference.html) for more information on how to write SSP docs.