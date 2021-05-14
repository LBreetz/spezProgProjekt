name := """Project_Breetz"""
organization := "com.LBreetz"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.5"


libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
)

herokuAppName in Compile := "fast-journey-46818"

//libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.41"
//libraryDependencies += jdbc

Compile / herokuProcessTypes := Map(
  "web" -> "target/universal/stage/bin/project_breetz -Dhttp.port=$PORT",
)

//"target/universal/stage/bin/project_breetz -Dhttp.port=56640",

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.LBreetz.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.LBreetz.binders._"
