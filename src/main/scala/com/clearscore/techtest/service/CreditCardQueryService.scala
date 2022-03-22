package com.clearscore.techtest.service

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.clearscore.techtest.config._
import com.clearscore.techtest.models._

import scala.concurrent.Future

trait CreditCardQueryService extends CreditCardQueryResource
  with Config
  with ServiceJsonProtocol {

  implicit val host = config.getString("development.http.host")

  implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "ClearScoreApp")
  implicit val userMarshaller: spray.json.RootJsonFormat[User] = jsonFormat3(User.apply)

  def runService(port: Int, cscUrl: String, scoredUrl: String): Future[Http.ServerBinding] = {

    //TODO: this is to come from CreditCardQueryResource
    //define a basic route
    val route: Route = (path("creditcards") & post) {
      entity(as[User]) { _ =>
        val response = CardQueryServiceResponse(value = Option(Iterable(CardOffer("CardProvider", "ACard", 23.4, 0.6))))
        complete("Here are some cards to look at")
      }
    }

    //Run the server
    Http().newServerAt(host, port).bind(route)
  }
}