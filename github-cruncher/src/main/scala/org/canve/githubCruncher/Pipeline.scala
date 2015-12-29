package org.canve.githubCruncher
import org.allenai.pipeline._
import org.allenai.pipeline.IoHelpers._
import java.io.File

object Pipeline extends ImplicitPersistenceSerializations {
  
  object PipelineImpl extends Pipeline {
    override def rootOutputUrl = {
        new File("out").toURI // AIP outputs directory
    }
  }

  case class GenerateProjectsList() 
    extends Producer[List[play.api.libs.json.JsValue]] 
    with Ai2StepInfo with GithubCrawler {
    
      override def create =  projectsList
  }
 
  PipelineImpl.Persist.Collection.asJson(GenerateProjectsList())
  
  def run = PipelineImpl.run("github processing pipeline")
  
}
