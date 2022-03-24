package com.clearscore.techtest.swagger

import com.clearscore.techtest.config.Config
import com.github.swagger.akka.model.Info
import com.clearscore.techtest.service.CreditCardOfferService
import com.github.swagger.akka.ui.SwaggerHttpWithUiService
import io.swagger.v3.oas.models.ExternalDocumentation

object SwaggerDocService extends SwaggerHttpWithUiService with Config {
  override val apiClasses = Set(classOf[CreditCardOfferService])
  override val host = config.getString("swagger.api.url")
  override val info: Info = Info(version = "1.0")
  private val docsUrl = config.getString("swagger.api.docs")
  override val externalDocs: Option[ExternalDocumentation] = Some(new ExternalDocumentation().description("Core Docs").url(docsUrl))

  override val unwantedDefinitions = Seq("Function1", "Function1RequestContextFutureRouteResult")
}
