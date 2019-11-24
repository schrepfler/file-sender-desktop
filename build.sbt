name := "file-sender-desktop"

version := "1.0"

scalaVersion := "2.13.1"

val akkaVersion = "2.6.0"
val scalaFXVersion = "12.0.2-R18"
val scalaFXMLVersion = "0.5"
val scalaTestVersion = "3.2.0-M1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "org.scalafx" %% "scalafx" % scalaFXVersion,
  "org.scalafx" %% "scalafxml-core-sfx8" % scalaFXMLVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
)

scalacOptions += "-Ymacro-annotations"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

// set the main class for 'sbt run'
mainClass in (Compile, run) := Some("filesender.FileSenderApp")

// set the main class for packaging the main jar
mainClass in (Compile, packageBin) := Some("filesender.FileSenderApp")

fork := true
