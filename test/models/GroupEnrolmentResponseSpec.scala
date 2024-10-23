/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import base.SpecBase
import generators.Generators
import models.GroupEnrolmentResponse.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, Json}

class GroupEnrolmentResponseSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Enrolment" - {
    "must deserilaise" - {
      "when json contains a service" in {
        forAll(nonEmptyString) {
          value =>
            val json = Json.parse(s"""
                 |{
                 |  "service" : "$value"
                 |}
                 |""".stripMargin)

            val result = json.validate[Enrolment]

            val expectedResult = Enrolment(value)

            result.get.mustBe(expectedResult)
        }
      }
    }

    "must fail to deserialise" - {
      "when json is in unexpected shape" in {
        forAll(nonEmptyString, nonEmptyString) {
          (key, value) =>
            val json = Json.parse(s"""
                 |{
                 |  "$key" : "$value"
                 |}
                 |""".stripMargin)

            val result = json.validate[Enrolment]

            result.mustBe(a[JsError])
        }
      }
    }
  }

  "Enrolments" - {
    "must deserilaise" - {
      "when json contains list of services" in {
        val json = Json.parse(s"""
             |{
             |  "enrolments" : [
             |    {
             |      "service" : "foo"
             |    },
             |    {
             |      "service" : "bar"
             |    }
             |  ]
             |}
             |""".stripMargin)

        val result = json.validate[Enrolments]

        val expectedResult = Enrolments(
          Seq(
            Enrolment("foo"),
            Enrolment("bar")
          )
        )

        result.get.mustBe(expectedResult)
      }
    }

    "must fail to deserialise" - {
      "when json is in unexpected shape" in {
        forAll(nonEmptyString, nonEmptyString) {
          (key, value) =>
            val json = Json.parse(s"""
                 |{
                 |  "$key" : "$value"
                 |}
                 |""".stripMargin)

            val result = json.validate[Enrolments]

            result.mustBe(a[JsError])
        }
      }
    }
  }

  "BadRequest" - {
    "must deserilaise" - {
      "when json contains a code" in {
        forAll(nonEmptyString) {
          value =>
            val json = Json.parse(s"""
                 |{
                 |  "code" : "$value"
                 |}
                 |""".stripMargin)

            val result = json.validate[BadRequest]

            val expectedResult = BadRequest(value)

            result.get.mustBe(expectedResult)
        }
      }
    }

    "must fail to deserialise" - {
      "when json is in unexpected shape" in {
        forAll(nonEmptyString, nonEmptyString) {
          (key, value) =>
            val json = Json.parse(s"""
                 |{
                 |  "$key" : "$value"
                 |}
                 |""".stripMargin)

            val result = json.validate[BadRequest]

            result.mustBe(a[JsError])
        }
      }
    }
  }
}
