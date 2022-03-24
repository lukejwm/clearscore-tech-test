package com.clearscore.techtest.swagger

import akka.actor.ActorSystem
import com.clearscore.techtest.config.Config
import com.github.swagger.akka.SwaggerHttpService

case class Swagger(system: ActorSystem) extends SwaggerHttpService with Config {

  override def apiClasses: Set[Class[_]] = ???
}