package com.clearscore.techtest.service

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.clearscore.techtest.models._
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

trait CreditCardOfferService extends ServiceJsonProtocol {
  implicit val actorSystem = ActorSystem(Behaviors.empty, "ClearScoreApp")
  implicit val userMarshaller: spray.json.RootJsonFormat[User] = jsonFormat3(User.apply)

  val log = LoggerFactory.getLogger("om.clearscore.techtest.service.CreditCardOfferService")

  def runService(host: String, port: Int, csCardsEndpoint: String, scoredCardsEndpoint: String): Future[Http.ServerBinding] = {
    log.info(s"Starting service on host: ${host}, port: ${port}")
    log.debug(s"CSCards Endpoint: ${csCardsEndpoint}")
    log.debug(s"ScoredCards Endpoint: ${scoredCardsEndpoint}")

    val route: Route = (path("creditcards") & post) {
      entity(as[User]) { user =>
        log.info(s"Responding to userId: ${user.name}")
        complete(findCreditCardDeals(user, csCardsEndpoint, scoredCardsEndpoint))
      }
    }

    Http().newServerAt(host, port).bind(route)
  }

  def findCreditCardDeals(user: User, csCardsEndpoint: String, scoredCardsEndpoint: String): Future[List[CardQueryServiceResponse]] = {
    for {
      csCardOffers <- fetchCsCardOffers(user, csCardsEndpoint)
      scoredCardOffers <- fetchScoredCardsOffers(user, scoredCardsEndpoint)
    } yield {
      //process CSCard offers
      val csCards = for (csCardOffer <- csCardOffers) yield {
        CardQueryServiceResponse("CSCards", csCardOffer.cardName, csCardOffer.apr, getSortingScore(csCardOffer.eligibility, csCardOffer.apr))
      }

      //process ScoredCard offers
      val scoredCards = for (scoredCardOffer <- scoredCardOffers) yield {
        CardQueryServiceResponse("ScoredCards", scoredCardOffer.card, scoredCardOffer.apr, getSortingScore(scoredCardOffer.approvalRating, scoredCardOffer.apr))
      }

      val allCards = csCards ::: scoredCards
      allCards.sortBy(c => c.cardScore)
    }
  }

  private def getSortingScore(eligibility: Double, apr: Double): Double = {
    val rawRating = eligibility * (1 / scala.math.pow(apr, 2))
    //ensure that a decimal number to precision 2 is returned (i.e. x.xx) so is readable to end user
    BigDecimal(rawRating).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  //method to make request to cscards endpoint and handle the response
  private def fetchCsCardOffers(user: User, endpoint: String): Future[List[CSCardsResponse]] = {
    val body = s"""{"name": "${user.name}", "creditScore": ${user.creditScore}}"""

    val csCardOffers = Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = endpoint,
        entity = HttpEntity(ContentTypes.`application/json`, body))).flatMap {
      case HttpResponse(StatusCodes.OK, _, entity, _) => Unmarshal(entity).to[List[CSCardsResponse]]
      case HttpResponse(status, _, entity, _) =>
        entity.toStrict(5.seconds).map(_.data.utf8String).flatMap(resultString => Future.failed(
          new IllegalStateException(
            s"Failed to get credit card offers from CSCards. URI: ${endpoint}, HTTP Status ${status}, entity: ${resultString}")))
    }

    csCardOffers.onComplete(res => res)
    csCardOffers
  }

  //method to make request to scored cards endpoint and handle the response
  private def fetchScoredCardsOffers(user: User, endpoint: String): Future[List[ScoredCardsResponse]] = {
    val body = s"""{"name": "${user.name}", "score": ${user.creditScore}, "salary": ${user.salary}}"""

    val scoredCardsOffers = Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = endpoint,
        entity = HttpEntity(ContentTypes.`application/json`, body))).flatMap {
      case HttpResponse(StatusCodes.OK, _, entity, _) => Unmarshal(entity).to[List[ScoredCardsResponse]]
      case HttpResponse(status, _, entity, _) =>
        entity.toStrict(5.seconds).map(_.data.utf8String).flatMap(resultString => Future.failed(
          new IllegalStateException(
            s"Failed to get credit card offers from ScoredCards. URI: ${endpoint}, HTTP Status ${status}, entity: ${resultString}")))
    }

    scoredCardsOffers.onComplete(res => res)
    scoredCardsOffers
  }
}