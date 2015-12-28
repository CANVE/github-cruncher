import com.scalapenos.sbt.prompt.SbtPrompt.autoImport._

lazy val org = sys.props.getOrElse("org", "canve")

lazy val commonSettings = Seq(
  promptTheme := Scalapenos,
  organization := org
)

/*
 * a conslidating root project definition - for overcoming sbt-eclipse/scala-ide limitations
 */
lazy val root = (project in file("."))
  .aggregate(pipeline, githubCruncher)
  .settings(commonSettings).settings(
    publishArtifact := false // no artifact to publish for the void root project itself
)

lazy val githubCruncher = (project in file("github-cruncher"))
  .dependsOn(pipeline)
  .disablePlugins(ScalariformPlugin, StylePlugin)
  .settings(commonSettings).settings(
    scalaVersion := "2.11.5",
    publishArtifact := false,
    libraryDependencies ++= Seq(

      /* slick */
      "com.typesafe.slick" %% "slick" % "3.1.1",
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
      "mysql" % "mysql-connector-java" % "5.1.38",
      "com.zaxxer" % "HikariCP-java6" % "2.3.9",

      /* json */
      "com.typesafe.play" %% "play-json" % "2.4.6",

      /* http client */
      "org.scalaj" %% "scalaj-http" % "2.2.0"
    ),
    slickAutoGenerate <<= slickCodeGenTask // register sbt command
    // sourceGenerators in Compile <+= slickCodeGenTask // register automatic code generation on every compile
)

// slick code generation task - from https://github.com/slick/slick-codegen-example/blob/master/project/Build.scala
lazy val slickAutoGenerate = TaskKey[Seq[File]]("slick-gen")

lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>

  val dbName = "github_crawler"
  val (user, password) = ("canve", "") // no password for this user

  val jdbcDriver = "com.mysql.jdbc.Driver"
  val slickDriver = "slick.driver.MySQLDriver"
  val url = s"jdbc:mysql://localhost:3306/$dbName?user=$user"

  val targetDir = "src/main/scala"
  val pkg = "org.canve.githubCruncher.mysql"

  toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, targetDir, pkg), s.log))

  val outputSourceFile = s"$targetDir/org/canve/githubCruncher/mysql/Tables.scala"
  println(scala.Console.GREEN + s"[info] slick code now auto-generated at $outputSourceFile" + scala.Console.RESET)
  Seq(file(outputSourceFile))
}

lazy val pipeline = (project in file("pipeline"))
