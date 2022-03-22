package com.clearscore.techtest

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.clearscore.techtest.config.Config
import com.clearscore.techtest.models._

object ClearScoreApp extends App
  with Config
  with ServiceJsonProtocol {

  val host = config.getString("development.http.host")
  val port = config.getInt("development.http.port")

  implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "ClearScoreApp")
  implicit val userMarshaller: spray.json.RootJsonFormat[User] = jsonFormat3(User.apply)

  val route: Route = (path("creditcards") & post) {
    entity(as[User]) { _ =>
      complete(CardQueryServiceResponse("CardProvider", "ACard", 23.4, 0.6))
    }
  }

  Http().newServerAt(host, port).bind(route)
}
