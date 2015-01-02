name := "scalaz-stream-contrib"

version := "0.0.0-SNAPSHOT"

scalaVersion := "2.11.4"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
)

scalacOptions in (Compile, doc) ++= Seq(
  "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
  "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath
)

libraryDependencies ++= Seq(
  "org.scalaz.stream" %% "scalaz-stream" % "0.6a",
  "org.scalacheck" %% "scalacheck" % "1.12.1" % "test"
)

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

scmInfo := Some(ScmInfo(url("https://github.com/fthomas/scalaz-stream-contrib"),
  "git@github.com:fthomas/scalaz-stream-contrib.git"))

initialCommands := """
  import scalaz._
  import scalaz.Scalaz._
  import scalaz.concurrent.Task
  import scalaz.stream._
  import eu.timepit.scalaz.stream.contrib._
"""
