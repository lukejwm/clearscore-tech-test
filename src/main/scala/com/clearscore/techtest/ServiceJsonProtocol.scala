package com.clearscore.techtest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import com.clearscore.techtest.models._

trait ServiceJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userFormat = jsonFormat3(User)
  implicit val cardQueryResponse = jsonFormat4(CardQueryServiceResponse)
}
