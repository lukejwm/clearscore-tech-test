package com.clearscore.techtest.service

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.clearscore.techtest.models._
import spray.json._

trait ServiceJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  //JSON Serialization
  implicit val userFormat = jsonFormat3(User)
  implicit val serviceResponseFormat = jsonFormat4(CardQueryServiceResponse)

  //Ensure that CSCards response format can allow for JSON serialization and deserialization
  implicit object cscCardsResponseFormat extends RootJsonFormat[CSCardsResponse] {

    override def read(jsonValue: JsValue): CSCardsResponse = {
      jsonValue.asJsObject.getFields("cardName", "apr", "eligibility") match {
        case Seq(JsString(cardName), JsNumber(apr), JsNumber(eligibility)) =>
           CSCardsResponse(cardName, apr.toDouble, eligibility.toDouble)

        case _ => throw DeserializationException("Expected credit card from CSCards")
      }
    }

    override def write(objValue: CSCardsResponse): JsObject = JsObject(
      "cardName" -> JsString(objValue.cardName),
      "apr" -> JsNumber(objValue.apr),
      "eligibility" -> JsNumber(objValue.eligibility)
    )
  }

  //Ensure that Scored Cards response format can allow for JSON serialization and deserialization
  implicit object scoredCardsResponseFormat extends RootJsonFormat[ScoredCardsResponse] {

    override def read(jsonValue: JsValue): ScoredCardsResponse = {
      jsonValue.asJsObject.getFields("card", "apr", "approvalRating") match {
        case Seq(JsString(card), JsNumber(apr), JsNumber(approvalRating)) =>
          ScoredCardsResponse(card, apr.toDouble, approvalRating.toDouble)

        case _ => throw new DeserializationException("Expected credit card from ScoredCards")
      }
    }

    override def write(objValue: ScoredCardsResponse): JsObject = JsObject(
      "card" -> JsString(objValue.card),
      "apr" -> JsNumber(objValue.apr),
      "approvalRating" -> JsNumber(objValue.approvalRating)
    )
  }
}
