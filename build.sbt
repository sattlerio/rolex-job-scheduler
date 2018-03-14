
name := "rolex-job-scheduler"
organization := "com.sattle"
description := "Wrapper for scheduled and planned tasks"
version := "1.0"

// Enables publishing to maven repo
publishMavenStyle := true

// Do not append Scala versions to the generated artifacts
crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

assemblyJarName in assembly := "rolex-job-scheduler.jar"
mainClass in assembly := Some(" com.sattler.rolexJobSchedulerApplication")

assemblyMergeStrategy in assembly := {
  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat
  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case ("io.netty.versions.properties" :: Nil) => MergeStrategy.discard
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
        MergeStrategy.discard
      case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.deduplicate
    }
  case PathList("javax", "inject", xs @ _*) => MergeStrategy.last
  case _ => MergeStrategy.deduplicate
}



libraryDependencies += "io.dropwizard" % "dropwizard-core" % "1.2.2"
libraryDependencies += "com.squareup.okhttp3" % "okhttp" % "3.10.0"
libraryDependencies += "org.knowm" % "dropwizard-sundial" % "1.1.0"
libraryDependencies += "org.mongodb.morphia" % "morphia" % "1.3.1"


libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test
libraryDependencies += "io.dropwizard" % "dropwizard-testing" % "1.2.2" % Test

testOptions += Tests.Argument(TestFrameworks.JUnit)


