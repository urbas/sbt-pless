logLevel := Level.Warn

// The Typesafe repository
resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype Public Repository" at "https://oss.sonatype.org/content/groups/public"
)

lazy val root = project.in( file(".") ).dependsOn( assemblyPlugin )

lazy val assemblyPlugin = uri("git://github.com/urbas/sbt-pless")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8.3")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.2.1")
