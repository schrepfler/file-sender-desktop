name := "file-sender-desktop"

version := "1.0"

scalaVersion := "2.11.7"

val akkaVersion = "2.4.0"
val scalaFXVersion = "8.0.60-R9"
val scalaTestVersion = "2.2.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "org.scalafx" %% "scalafx" % scalaFXVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
