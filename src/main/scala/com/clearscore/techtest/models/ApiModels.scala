package com.clearscore.techtest.models

import io.swagger.v3.oas.annotations._

//TODO: Annotate these as APIModel using Swagger

abstract class Response(error: Option[String], exception: Option[String])

//What a user will input into the service
final case class User(name: String, creditScore: Int, salary: Int)

//What will be output to the user
final case class CardOffer(provider: String, name: String, apr: Double, cardScore: Double)

//The service response
final case class CardQueryServiceResponse(value: Option[Iterable[CardOffer]] = None,
                                          error: Option[String] = None,
                                          exception: Option[String] = None) extends Response(error, exception)

//Objects to be marshalled into JSON strings for sending POST requests to providers
final case class UserQueryFormat1(name: String, creditScore: Int)

final case class UserQueryFormat2(name: String, score: Int, salary: Int)

//Objects to receive JSON responses from card providers
final case class CSCardsOffer(cardName: String, apr: Double, eligibility: Double)

final case class CSCardsResponse(value: Option[Iterable[CSCardsOffer]] = None,
                                 error: Option[String] = None,
                                 exception: Option[String] = None)  extends Response(error, exception)

final case class ScoredCardsOffer(card: String, apr: Double, approvalRating: Double)

final case class ScoredCardsResponse(value: Option[Iterable[ScoredCardsOffer]] = None,
                                     error: Option[String] = None,
                                     exception: Option[String] = None)  extends Response(error, exception)