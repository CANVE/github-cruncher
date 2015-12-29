package org.canve.githubCruncher
import scalaj.http._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.util.{Try, Success, Failure}

trait GithubCrawler {  

  def projectsList: List[JsValue] =
    
    Try(
      Http("https://api.github.com/search/repositories")
      .param("q", "language:scala")
      .param("sort", "forks")
      .asString) match {
      
        case Failure(e) => 
          throw new Exception(s"""github api call failed - have we been rate limited? failure details: \n$e""")
            
        case Success(response) => 
            //println(response.body)
            println(response.isSuccess)
            val asJson: JsValue = Json.parse(response.body)
            //println(Json.prettyPrint(asJson))
            (asJson \ "items").as[JsArray]
            .as[List[JsValue]]
         
      }
}
