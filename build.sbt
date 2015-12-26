import com.scalapenos.sbt.prompt.SbtPrompt.autoImport._

lazy val org = sys.props.getOrElse("org", "canve")

lazy val commonSettings = Seq(
  promptTheme := Scalapenos,
  organization := org
)

lazy val githubCruncher = (project in file("."))
  .settings(commonSettings).settings(
    scalaVersion := "2.11.7",
    publishArtifact := false,
    libraryDependencies ++= Seq(

      /* slick */
      "com.typesafe.slick" %% "slick" % "3.1.1",
      "org.slf4j" % "slf4j-nop" % "1.6.4",

      /* json */
      "com.typesafe.play" %% "play-json" % "2.4.6",

      /* http client */
      "org.scalaj" %% "scalaj-http" % "2.2.0"
    )
)