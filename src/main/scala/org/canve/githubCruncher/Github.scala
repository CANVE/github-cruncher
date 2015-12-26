package org.canve.githubCruncher
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import mysql.Tables._

object app extends App {
  val g = Github
}

object Github {
  // Slick Database configuration
  
  object slick { 
    // TODO: switch to sbt-build-info plugin for sharing these values with the slick generation task of build.sbt
    // TODO: for concurrency add a connection pool: http://slick.typesafe.com/doc/3.1.1/database.html
    // TODO: for the above TODO's might need to switch database factory methods - http://slick.typesafe.com/doc/3.1.1/api/index.html#slick.jdbc.JdbcBackend$DatabaseFactoryDef@forConfig(String,Config,Driver):Database 
    val dbName = "github_crawler" 
    val user = "canve"
    val db = Database.forURL(
      driver = "com.mysql.jdbc.Driver",
      url = s"jdbc:mysql://localhost:3306/$dbName", 
      user = user,
      keepAliveConnection = true)
  }
  
  implicit val session: Session = slick.db.createSession
  
  //slick.db.run()
}