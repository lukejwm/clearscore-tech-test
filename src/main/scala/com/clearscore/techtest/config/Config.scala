package com.clearscore.techtest.config

import com.typesafe.config.ConfigFactory

trait Config {
  //get env from the system scala environment variable or set default
  val env = if (System.getenv("SCALA_ENV") == null) "development" else System.getenv("SCALA_ENV")

  val config = ConfigFactory.load()

  def apply() = config.getConfig(env)
}
