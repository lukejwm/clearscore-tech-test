package com.clearscore.techtest.models

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse

//What a user will input into the service
@Parameter
final case class User(name: String, creditScore: Int, salary: Int)

//The service response
@ApiResponse
final case class CardQueryServiceResponse(provider: String, name: String, apr: Double, cardScore: Double)

//Objects to receive JSON responses from card providers
@ApiResponse
final case class CSCardsResponse(cardName: String, apr: Double, eligibility: Double)

@ApiResponse
final case class ScoredCardsResponse(card: String, apr: Double, approvalRating: Double)