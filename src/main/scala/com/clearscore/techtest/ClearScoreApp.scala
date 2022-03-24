package com.clearscore.techtest

import cats.effect.IO
import com.clearscore.techtest.config.Config
import com.clearscore.techtest.service.CreditCardOfferService
import io.jobial.sclap.CommandLineApp
import io.jobial.sclap.core.CommandLine

object ClearScoreApp extends CommandLineApp
  with Config
  with CreditCardOfferService {

  override def run: CommandLine[Any] = command
    .header("Credit Card Recommendation Service")
    .description("ClearScore Tech Interview Task") {
      for {
        //command line options to be provided by end user or through config setting by default
        port <- param[Int].label("HTTP_PORT").default(config.getInt(s"${env}.http.port")).description("The port to expose your service on")
        cscUrl <- param[String].label("CSCARDS_ENDPOINT").default(config.getString(s"${env}.providers.url.cscards")).description("The base URL for CSCards")
        scoredUrl <- param[String].label("SCOREDCARDS_ENDPOINT").default(config.getString(s"${env}.providers.url.scoredcards")).description("The base URL for ScoredCards")
      } yield IO {
        //normally you would define the host in the command line options, but this was not asked for in problem spec, so extract from config
        val host = config.getString(s"${env}.http.host")
        runService(host, port, cscUrl, scoredUrl)
      }
    }
}
