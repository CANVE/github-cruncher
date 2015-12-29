package org.canve.githubCruncher
import mysql.DB
import scalaj.http._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.util.{Try, Success, Failure}
import org.allenai.pipeline._
import org.allenai.pipeline.IoHelpers._
import java.io.File

case class Project(
  searchScore: Float,
  isFork: Boolean,
  forksCount: Int,
  description: String,
  fullName: String,
  sshCloneUrl: String,
  httpCloneUrl: String,
  url: String,
  languagesApiUrl: String) {
  
  //def cloneRepo {}
  
}
  
object app extends App with GithubCrawler {
  
  val db = DB
  
  //println(crawl.get.toString)
  Trial
}

object Trial {
  
  object MyPipeline extends Pipeline {
    override def rootOutputUrl = {
        new File("out").toURI
    }
  }

  case class GenerateProjectsList() extends Producer[List[Project]] with Ai2StepInfo with GithubCrawler {
    override def create: List[Project] = {
      println("generating project list")
      crawl.get    
    }
  }
 
  implicit object ProjectSerialize 
    extends org.allenai.pipeline.StringSerializable[Project] with spray.json.JsonFormat[Project] {

    def read(json: spray.json.JsValue): Project = ???
    def write(obj: Project): spray.json.JsValue = ???
    
    def fromString(s: String): org.canve.githubCruncher.Project = ???
    def toString(p: Project): String = p.toString()
  }

  MyPipeline.Persist.Collection.asText(GenerateProjectsList())
  MyPipeline.run("github processing pipeline")
}

trait GithubCrawler {  

  def crawl: Option[List[Project]] = 
    Try(
      Http("https://api.github.com/search/repositories")
      .param("q", "language:scala")
      .param("sort", "forks")
      .asString) match {
      
        case Failure(e) => 
          println(s"""github api call failed - have we been rate limited? failure details: \n$e""")
          None
            
        case Success(response) => 
            //println(response.body)
            println(response.isSuccess)
            val asJson: JsValue = Json.parse(response.body)
            //println(Json.prettyPrint(asJson))
            val items: List[JsValue] = 
              (asJson \ "items").as[JsArray]
              .as[List[JsValue]]
            
            val projects = items map { item => 
              Project(
                searchScore = (item \ "score").as[Float],
                isFork = (item \ "fork").as[Boolean],
                forksCount = (item \ "forks").as[Int],
                description = (item \"description").as[String],
                fullName = (item \ "full_name").as[String],
                sshCloneUrl = (item \"ssh_url").as[String],
                httpCloneUrl = (item \"clone_url").as[String],
                url = (item \"html_url").as[String],
                languagesApiUrl = (item \"languages_url").as[String]
              )
            }
            
            Some(projects)
      }
}
