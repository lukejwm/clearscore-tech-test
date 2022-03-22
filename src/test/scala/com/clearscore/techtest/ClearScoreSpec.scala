package com.clearscore.techtest

import com.clearscore.techtest.models._
import org.scalatest.freespec.AnyFreeSpec

class ClearScoreSpec extends AnyFreeSpec {

  "Each credit card provider" - {
    "CSCards can respond with data" in {
      val mockUser = UserQueryFormat1("John Smith", 500)
    }

    "ScoredCards can respond with data" in {
      val mockUser = UserQueryFormat2("John Smith", 341, 18500)
    }
  }

  "Credit card query service" - {
    "return a set of credit card offers" in {
      val mockUser = User("John Smith", 500, 28000)
    }
  }
}
