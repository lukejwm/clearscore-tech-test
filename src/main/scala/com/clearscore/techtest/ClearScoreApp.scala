package com.clearscore.techtest

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling._
import com.clearscore.techtest.config.Config
import com.clearscore.techtest.models._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

object ClearScoreApp extends App
  with Config
  with ServiceJsonProtocol {

  val host = config.getString("development.http.host")
  val port = config.getInt("development.http.port")

  implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "ClearScoreApp")
  implicit val userMarshaller: spray.json.RootJsonFormat[User] = jsonFormat3(User.apply)

  val csCardsEndpoint = config.getString("development.providers.url.cscards")
  val scoredCardsEndpoint = config.getString("development.providers.url.scoredcards")

  def csCardsRequest(): Future[HttpResponse] =
    Http(actorSystem).singleRequest(
      HttpRequest(
        HttpMethods.POST,
        csCardsEndpoint,
        entity = HttpEntity(ContentTypes.`application/json`, "{\"name\": \"John Smith\", \"creditScore\": 500}")
      )
    )

  def scoredCardsRequest() = {
    val response = Http(actorSystem).singleRequest(
      HttpRequest(
        uri = scoredCardsEndpoint,
        method = HttpMethods.POST,
        entity = HttpEntity(ContentTypes.`application/json`, "{\"name\": \"John Smith\", \"score\": 500, \"salary\": 18500}")
      )
    )

    response onComplete {
      case Success(res) =>
        val scoredCardsRes: Future[ScoredCardsResponse] = Unmarshal(res.entity).to[ScoredCardsResponse]
        println(scoredCardsRes)
      case Failure(es) => println("This Failed")
    }
  }

  scoredCardsRequest()

//  val route: Route = (path("creditcards") & post) {
//    entity(as[User]) { _ =>
//      complete(CardQueryServiceResponse("CardProvider", "ACard", 23.4, 0.6))
//    }
//  }
//
//  Http().newServerAt(host, port).bind(route)
}
