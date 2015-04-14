name := "platform-snapshot"

organization := "com.stys"

version := "1.1.1"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.10.4", "2.11.2")

libraryDependencies ++= Seq(
	"net.sourceforge.htmlunit" % "htmlunit" % "2.15"
)     

lazy val root = (project in file(".")).enablePlugins(PlayJava)
