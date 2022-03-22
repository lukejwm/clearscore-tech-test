package com.clearscore.techtest

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

import com.clearscore.techtest.config.Config

object ClearScoreApp extends App with Config {
  val host = config.getString("development.http.host")
  val port = config.getInt("development.http.port")

  implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "ClearScoreApp")

  val route: Route = (path("creditcards") & post) {
    complete("You are viewing credit card offers")
  }

  Http().newServerAt(host, port).bind(route)
}
