addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.0.4")
addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
)
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
