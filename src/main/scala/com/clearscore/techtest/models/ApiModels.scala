package com.clearscore.techtest.models

//What a user will input into the service
final case class User(name: String, creditScore: Int, salary: Int)

//The service response
final case class CardQueryServiceResponse(provider: String, name: String, apr: Double, cardScore: Double)

//Objects to receive JSON responses from card providers
final case class CSCardsResponse(cardName: String, apr: Double, eligibility: Double)

final case class ScoredCardsResponse(card: String, apr: Double, approvalRating: Double)