package org.canve.githubCruncher
import scalaj.http._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.util.{Try, Success, Failure}

trait GithubCrawler {  

  def projectsList: List[JsValue] = {
    
    Try(Http("https://api.github.com/search/repositories")
      .param("q", "language:scala")
      .param("sort", "forks")
      .asString) match {

      case Failure(e) => 
      throw new Exception(s"""github api call failed - have we been rate limited? failure details: \n$e""")
      
      case Success(response) => 

        if (!response.isSuccess) 
          throw new Exception(s"""github api call failed - have we been rate limited? failure details: \n$response""")

        val asJson: JsValue = Json.parse(response.body)
        val headers: Map[String, IndexedSeq[String]] = response.headers
        val linkHeaders = parseGithubLinkHeader(headers("Link"))

        //println(Json.prettyPrint(asJson))
        (asJson \ "items")
          .as[JsArray]
          .as[List[JsValue]]
    }
  }
  
  /*
   *  parses the link header field (http://tools.ietf.org/html/rfc5988) returned by Github's api 
   *  (c.f. https://developer.github.com/guides/traversing-with-pagination/)
   */
  private def parseGithubLinkHeader(linkHeader: IndexedSeq[String]): Map[String, String] = {
    assert(linkHeader.size == 1)
    linkHeader.head.split(',').map { linkEntry =>
      val entryPart = linkEntry.split(';')
      assert(entryPart.size == 2)
      val rel = entryPart(1).replace(" rel=\"", "").replace("\"", "")
      val url = entryPart(0).replace("<", "").replace(">", "")
      (rel, url)
    }.toMap
  }
}