package com.clearscore.techtest.service

import akka.http.scaladsl.model.{HttpResponse, StatusCode}
import akka.http.scaladsl.server.{Directives, Route}
import com.clearscore.techtest.models._
import io.swagger.v3.oas.annotations._
import jakarta.ws.rs.{POST, Path}

import scala.concurrent.Future
import scala.util.{Failure, Success}

@POST
@Path("/creditcards")
@Operation(summary = "Post user information to providers to fetch credit card offers")
trait CreditCardQueryResource {
//  this: ServiceJsonProtocol with Directives =>

//  def creditCardQuery: Route = (path("creditcards") & post) {
//    entity(as[User]) { user =>
//      val userQueryCsCards = UserQueryFormat1(user.name, user.creditScore)
//      val userQueryScoredCards = UserQueryFormat2(user.name, user.creditScore, user.salary)
//      onComplete(cCreditCardOffers(userQueryCsCards, userQueryScoredCards)) {
//        case Success(result) =>
//          complete("Success")
////          val resp = CardQueryServiceResponse("", "", 0.0, 0.0)
////          complete(resp)
//        case Failure(throwable) =>
//            complete("Failure")
//          //val resp = CardQueryServiceResponse(error = Some())
//      }
//    }
//  }

  def cCreditCardOffers(query1: UserQueryFormat1, query2: UserQueryFormat2): Future[HttpResponse] = {
     null
  }
}

//  def csCardsRequest(): Future[HttpResponse] =
//    Http(actorSystem).singleRequest(
//      HttpRequest(
//        HttpMethods.POST,
//        csCardsEndpoint,
//        entity = HttpEntity(ContentTypes.`application/json`, "{\"name\": \"John Smith\", \"creditScore\": 500}")
//      )
//    )
//
//  def scoredCardsRequest() = {
//    val response = Http(actorSystem).singleRequest(
//      HttpRequest(
//        uri = scoredCardsEndpoint,
//        method = HttpMethods.POST,
//        entity = HttpEntity(ContentTypes.`application/json`, "{\"name\": \"John Smith\", \"score\": 500, \"salary\": 18500}")
//      )
//    )
//
////    response onComplete {
////      case Success(res) =>
////        val scoredCardsRes: Future[ScoredCardsResponse] = Unmarshal(res.entity).to[ScoredCardsResponse]
////        println(scoredCardsRes)
////      case Failure(es) => println("This Failed")
////    }
//  }
//