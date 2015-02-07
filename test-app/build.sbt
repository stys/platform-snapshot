name := "platform-snapshot-test-app"

organization := "com.stys"

version := "1.1.0"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
	"com.stys" %% "platform-snapshot" % "1.1.0",
	javaCore
)     

lazy val root = (project in file(".")).enablePlugins(PlayJava)

