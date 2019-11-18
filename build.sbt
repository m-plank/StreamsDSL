lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "streams.dsl",
      scalaVersion := "2.12.7",
      version := "0.1.0-SNAPSHOT"
    )
  ),
  name := "StreamsDSL",
  resolvers += Resolver.mavenLocal,
  testScalastyle := scalastyle.in(Compile).toTask("").value,
  (test in Test) := ((test in Test) dependsOn testScalastyle).value,
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % "2.0.0",
    "org.typelevel" %% "cats-free" % "2.0.0",
    "com.typesafe.akka" %% "akka-stream" % "2.6.0",
    "co.fs2" %% "fs2-core" % "1.0.4",
    "co.fs2" %% "fs2-io" % "1.0.4",
    "org.typelevel" %% "cats-tagless-macros" % "0.10",
    "org.scalatest" %% "scalatest" % "3.0.8" % "test",
    "com.ironcorelabs" %% "cats-scalatest" % "3.0.0" % "test"
  ),
  addCommandAlias("co", ";clean;coverage;test;coverageReport")
)
lazy val testScalastyle = taskKey[Unit]("testScalastyle")
