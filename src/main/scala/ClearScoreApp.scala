//akka
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
//akka http
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._


object ClearScoreApp extends App {
  val host = "127.0.0.1"  //run service on localhost

  implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "ClearScoreApp")

  val route: Route = (path("creditcards") & post) {
    complete("You are viewing credit card offers")
  }

  Http().newServerAt(host, 8080).bind(route)
}
