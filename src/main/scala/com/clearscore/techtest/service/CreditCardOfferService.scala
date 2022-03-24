package com.clearscore.techtest.service

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.clearscore.techtest.ClearScoreApp.log
import com.clearscore.techtest.config._
import com.clearscore.techtest.models._
import org.slf4j.LoggerFactory
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

trait CreditCardOfferService extends ServiceJsonProtocol {

//  implicit val actorSystem = ActorSystem(Behaviors.empty, "ClearScoreApp")
//  implicit val userMarshaller: spray.json.RootJsonFormat[User] = jsonFormat3(User.apply)

  val log = LoggerFactory.getLogger("om.clearscore.techtest.service.CreditCardOfferService")

  def runService(host: String, port: Int, csCardsEndpoint: String, scoredCardsEndpoint: String) = {
    log.info(s"Starting service on host: ${host}, port: ${port}")
    log.debug(s"CSCards Endpoint: ${csCardsEndpoint}")
    log.debug(s"ScoredCards Endpoint: ${scoredCardsEndpoint}")
  }

//  def fetchCsCardOffers(user: User, endpoint: String)(implicit executionContext: ExecutionContext): Future[CSCardsResponse] = {
//    val body = s"""{"name": "${user.name}", "creditScore": ${user.creditScore}}"""
//
//    val csCardOffers = Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = endpoint,
//      entity = HttpEntity(ContentTypes.`application/json`, body))).flatMap {
//      case HttpResponse(StatusCodes.OK, _, entity, _) =>
//        entity.toStrict(5.seconds).map(strictEntity => JsonParser(ParserInput(strictEntity.data.utf8String)))
//          .map(_.convertTo[CSCardsResponse])
//
//      case HttpResponse(status, _, entity, _) =>
//        entity.toStrict(5.seconds).map(_.data.utf8String).flatMap(resultString => Future.failed(new IllegalStateException(
//          s"Failed to get credit card offers from CSCards. URI: ${endpoint}, HTTP Status ${status}, entity: ${resultString}"
//        )))
//    }
//
//    //TODO: ensure this works and use Success/Failure cases for this
//    csCardOffers.onComplete(res => res)
//    csCardOffers
//  }
//
//  def fetchScoredCardsOffers(user: User, endpoint: String)(implicit executionContext: ExecutionContext): Future[ScoredCardsResponse] = {
//    val body = s"""{"name": "${user.name}, "score": ${user.creditScore}, "salary": ${user.salary}}"""
//
//    val scoredCardsOffers = Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = endpoint,
//      entity = HttpEntity(ContentTypes.`application/json`, body))).flatMap {
//      case HttpResponse(StatusCodes.OK, _, entity, _) =>
//        entity.toStrict(5.seconds).map(strictEntity => JsonParser(ParserInput(strictEntity.data.utf8String)))
//          .map(_.convertTo[ScoredCardsResponse])
//
//      case HttpResponse(status, _, entity, _) =>
//        entity.toStrict(5.seconds).map(_.data.utf8String).flatMap(resultString => Future.failed(new IllegalStateException(
//          s"Failed to get credit card offers from CSCards. URI: ${endpoint}, HTTP Status ${status}, entity: ${resultString}"
//        )))
//    }
//
//    //TODO: ensure this works and use Success/Failure cases for this
//    scoredCardsOffers.onComplete(res => res)
//    scoredCardsOffers
//  }
}