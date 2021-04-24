name := """Project_Breetz"""
organization := "com.LBreetz"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.5"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

herokuAppName in Compile := "fast-journey-46818"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.LBreetz.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.LBreetz.binders._"
