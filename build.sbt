lazy val root = (project in file("."))
  .settings(organization := "streams.dsl", name := "StreamsDSL")
  .aggregate(core, examples)

lazy val core = project
  .settings(
    inThisBuild(
      List(
        scalaVersion := "2.12.7",
        version := "0.1.0-SNAPSHOT",
        libraryDependencies ++= Seq(
          "org.typelevel" %% "cats-core" % "2.0.0",
          "org.typelevel" %% "cats-effect" % "2.0.0",
          "org.typelevel" %% "cats-free" % "2.0.0",
          "com.typesafe.akka" %% "akka-stream" % "2.6.0",
          "co.fs2" %% "fs2-core" % "1.0.4",
          "co.fs2" %% "fs2-io" % "1.0.4",
          "org.typelevel" %% "cats-tagless-macros" % "0.10",
          "org.scalatest" %% "scalatest" % "3.0.8" % "test",
          "com.ironcorelabs" %% "cats-scalatest" % "3.0.0" % "test"
        )
      )
    ),
    resolvers += Resolver.mavenLocal,
    testScalastyle := scalastyle.in(Compile).toTask("").value,
    (test in Test) := ((test in Test) dependsOn testScalastyle).value,
    addCommandAlias("co", ";clean;coverage;test;coverageReport")
  )

lazy val examples = project.dependsOn(core)

lazy val testScalastyle = taskKey[Unit]("testScalastyle")
