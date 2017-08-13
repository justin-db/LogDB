lazy val root = (project in file(".")).settings(
   name := "logdb",
   version := "0.0.1",
   scalaVersion := "2.12.2",
   libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
