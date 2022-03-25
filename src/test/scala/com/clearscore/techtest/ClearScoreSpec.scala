package com.clearscore.techtest

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.clearscore.techtest.service.CreditCardOfferService
import com.clearscore.techtest.models._
import com.clearscore.techtest.config.Config
import com.clearscore.techtest.models.User
import org.scalatest.BeforeAndAfterAll
import org.scalatest.freespec.AnyFreeSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ClearScoreSpec extends AnyFreeSpec
  with CreditCardOfferService
  with Config
  with BeforeAndAfterAll {

  private val testHost = config.getString("test.http.host")
  private val testPort = config.getInt("test.http.port")
  private val ownServiceUrl = config.getString("test.local.service")
  private val cscUrl = config.getString("test.providers.url.cscards")
  private val scrdUrl = config.getString("test.providers.url.scoredcards")

  private val mockUser = User("John Smith", 700, 25000)

  //Ensure config is initialised correctly and start the service
  override def beforeAll(): Unit = {
    require(config.isResolved)

    assert(testHost == "127.0.0.1")
    assert(testPort == 4200)
    assert(cscUrl == "https://app.clearscore.com/api/global/backend-tech-test/v1/cards")
    assert(scrdUrl == "https://app.clearscore.com/api/global/backend-tech-test/v2/creditcards")

    runService(testHost, testPort, cscUrl, scrdUrl)
  }

  "Each credit card provider" - {
    "CSCards can respond with data" in {
      val result = fetchCsCardOffers(mockUser, cscUrl)
      for (item <- result) { assert(item.nonEmpty) }
    }

    "ScoredCards can respond with data" in {
      val result = fetchScoredCardsOffers(mockUser, scrdUrl)
      for (item <- result) { assert(item.nonEmpty) }
    }
  }

  "Credit card query service" - {
    "return a set of credit card offers" in {
      val mockUser = """{"name:" "John Smith", "creditScore": 700, "salary": 25000}"""
      val result = Http().singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = ownServiceUrl,
        entity = HttpEntity(ContentTypes.`application/json`, mockUser)))flatMap { response =>
          assert(response.status == StatusCodes.OK)
          Unmarshal(response.entity).to[List[CardQueryServiceResponse]]
      }

      for (item <- result) { assert(item.nonEmpty) }
    }
  }

  //kill the system after the tests
  override def afterAll(): Unit = {
    system.terminate()
  }
}
