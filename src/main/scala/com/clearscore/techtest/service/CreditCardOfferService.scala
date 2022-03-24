package com.clearscore.techtest.service

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.clearscore.techtest.models._
import com.clearscore.techtest.swagger.SwaggerDocService
import org.slf4j.LoggerFactory
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.{Consumes, POST, Path, Produces}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

trait CreditCardOfferService extends ServiceJsonProtocol {
  implicit val system = ActorSystem(Behaviors.empty, "ClearScoreApp")
  implicit val userMarshaller: spray.json.RootJsonFormat[User] = jsonFormat3(User.apply)

  val log = LoggerFactory.getLogger("om.clearscore.techtest.service.CreditCardOfferService")

  @POST
  @Path("/creditcards")
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(
    summary = "Fetch list of credit card offers from multiple providers",
    description = "Return set of of credit card offers sorted by card score to user, given user name, credit score and salary",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Successful response",
        content = Array(new Content(schema = new Schema(implementation = classOf[CardQueryServiceResponse])))),
      new ApiResponse(responseCode = "400", description = "Malformed request (user name, credit score or salary are missing)"),
      new ApiResponse(responseCode = "500", description = "Internal server error")
    )
  )
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

    val swaggerRoute = SwaggerDocService.routes

    Http().newServerAt(host, port).bind(route ~ swaggerRoute)
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
      log.info(s"Fetching the following credit card deals for user: ${user}")
      allCards.sortBy(c => c.cardScore)
    }
  }

  private def getSortingScore(eligibility: Double, apr: Double): Double = {
    val rawRating = eligibility * (1 / scala.math.pow(apr, 2))
    //ensure that a decimal number to precision 2 is returned (i.e. x.xx) so is readable to end user
    BigDecimal(rawRating).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(
    summary = "Fetch list of credit card offers from CSCards at specified endpoint",
    description = "Return set of of credit card offers from CSCards given user name and credit score",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Successful response",
        content = Array(new Content(schema = new Schema(implementation = classOf[CSCardsResponse])))),
      new ApiResponse(responseCode = "400", description = "Malformed request (user name or credit score are missing)"),
      new ApiResponse(responseCode = "404", description = "Endpoint not available")
    )
  )
  private def fetchCsCardOffers(user: User, endpoint: String): Future[List[CSCardsResponse]] = {
    val body = s"""{"name": "${user.name}", "creditScore": ${user.creditScore}}"""

    log.info(s"Fetching credit card deals from provider with endpoint: ${endpoint} with HTTP POST with following body: ${body}")
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
    log.info(s"Successful fetched data from provider at endpoint: ${endpoint} with data: ${csCardOffers.value}")
    csCardOffers
  }

  @POST
  @Consumes(Array(MediaType.APPLICATION_JSON))
  @Produces(Array(MediaType.APPLICATION_JSON))
  @Operation(
    summary = "Fetch list of credit card offers from ScoredCards at specified endpoint",
    description = "Return set of of credit card offers sorted by card score to user, given use name, credit score and salary",
    responses = Array(
      new ApiResponse(responseCode = "200", description = "Successful response",
        content = Array(new Content(schema = new Schema(implementation = classOf[ScoredCardsResponse])))),
      new ApiResponse(responseCode = "400", description = "Malformed request (user name, credit score or salary are missing)"),
      new ApiResponse(responseCode = "404", description = "Endpoint not available")
    )
  )
  private def fetchScoredCardsOffers(user: User, endpoint: String): Future[List[ScoredCardsResponse]] = {
    val body = s"""{"name": "${user.name}", "score": ${user.creditScore}, "salary": ${user.salary}}"""

    log.info(s"Fetching credit card deals from provider with endpoint: ${endpoint} with HTTP POST with following body: ${body}")
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
    log.info(s"Successful fetched data from provider at endpoint: ${endpoint} with data: ${scoredCardsOffers.value}")
    scoredCardsOffers
  }
}