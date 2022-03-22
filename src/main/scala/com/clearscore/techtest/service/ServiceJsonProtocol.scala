package com.clearscore.techtest.service

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.clearscore.techtest.models._
import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol.iterableFormat

trait ServiceJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  //JSON Serialization
  implicit val userFormat = jsonFormat3(User)
  implicit val cardQueryResponse = jsonFormat3(CardQueryServiceResponse)
}
