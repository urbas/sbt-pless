import sbt._

logLevel := Level.Warn

resolvers += "Sonatype Public Repository" at "https://oss.sonatype.org/content/groups/public"

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8.3")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.2.1")

addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.5")

addSbtPlugin("si.urbas" % "sbt-pless" % "0.0.10")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")