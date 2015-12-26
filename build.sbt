lazy val root = (project in file("."))
  .settings(
    scalaVersion := "2.11.7",
    publishArtifact := false,
    libraryDependencies ++= Seq(

      /* slick */
      "com.typesafe.slick" %% "slick" % "3.1.1",
      "org.slf4j" % "slf4j-nop" % "1.6.4",

      /* json */
      "com.typesafe.play" %% "play-json" % "2.4.6"
    )
)