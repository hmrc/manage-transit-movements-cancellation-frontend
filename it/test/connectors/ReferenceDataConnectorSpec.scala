/*
 * Copyright 2023 HM Revenue & Customs
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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import connectors.ReferenceDataConnectorSpec._
import itbase.{ItSpecBase, WireMockServerHandler}
import models.CustomsOffice
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends ItSpecBase with WireMockServerHandler {

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  private val baseUrl = "customs-reference-data/test-only"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.customs-reference-data.port" -> server.port())

  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]
  private val code                                   = "AD000001"

  "Reference Data" - {

    "getCustomsOffice" - {
      val url = s"/$baseUrl/lists/CustomsOffices?data.id=$code"

      "should handle a 200 response for customs office with code end point with valid phone number" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(customsOfficeResponseJsonWithPhone))
        )

        val expectedResult = CustomsOffice("AD000001", "CUSTOMS OFFICE SANT JULIÀ DE LÒRIA", "AD", Some("+ (376) 84 1090"))

        connector.getCustomsOffice(code).futureValue mustBe expectedResult
      }

      "should handle a 200 response for customs office with code end point with no phone number" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(customsOfficeResponseJsonWithOutPhone))
        )

        val expectedResult = CustomsOffice("AD000001", "CUSTOMS OFFICE SANT JULIÀ DE LÒRIA", "AD", None)

        connector.getCustomsOffice(code).futureValue mustBe expectedResult
      }

      "should throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getCustomsOffice(code))
      }

      "should handle client and server errors for customs office end point" in {
        checkErrorResponse(url, connector.getCustomsOffice(code))
      }
    }
  }

  private def checkNoReferenceDataFoundResponse(url: String, result: => Future[_]): Assertion = {
    server.stubFor(
      get(urlEqualTo(url))
        .willReturn(okJson(emptyResponseJson))
    )

    whenReady[Throwable, Assertion](result.failed) {
      _ mustBe a[NoReferenceDataFoundException]
    }
  }

  private def checkErrorResponse(url: String, result: => Future[_]): Assertion = {
    val errorResponses: Gen[Int] = Gen.chooseNum(400: Int, 599: Int)

    forAll(errorResponses) {
      errorResponse =>
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(errorResponse)
            )
        )

        whenReady[Throwable, Assertion](result.failed) {
          _ mustBe an[Exception]
        }
    }
  }
}

object ReferenceDataConnectorSpec {

  private val customsOfficeResponseJsonWithPhone: String =
    """
      |{
      | "data" :
      | [
      |    {
      |      "languageCode": "ES",
      |      "name": "ADUANA DE ST. JULIÀ DE LÒRIA",
      |      "phoneNumber": "+ (376) 84 1090",
      |      "id": "AD000001",
      |      "countryId": "AD",
      |      "roles": [
      |        {
      |          "role": "AUT"
      |        },
      |        {
      |          "role": "DEP"
      |        },
      |        {
      |          "role": "DES"
      |        },
      |        {
      |          "role": "TRA"
      |        }
      |      ]
      |    },
      |    {
      |      "languageCode": "EN",
      |      "name": "CUSTOMS OFFICE SANT JULIÀ DE LÒRIA",
      |      "phoneNumber": "+ (376) 84 1090",
      |      "id": "AD000001",
      |      "countryId": "AD",
      |      "roles": [
      |        {
      |          "role": "AUT"
      |        },
      |        {
      |          "role": "DEP"
      |        },
      |        {
      |          "role": "DES"
      |        },
      |        {
      |          "role": "TRA"
      |        }
      |      ]
      |    },
      |    {
      |      "languageCode": "FR",
      |      "name": "BUREAU DE SANT JULIÀ DE LÒRIA",
      |      "phoneNumber": "+ (376) 84 1090",
      |      "id": "AD000001",
      |      "countryId": "AD",
      |      "roles": [
      |        {
      |          "role": "AUT"
      |        },
      |        {
      |          "role": "DEP"
      |        },
      |        {
      |          "role": "DES"
      |        },
      |        {
      |          "role": "TRA"
      |        }
      |      ]
      |    }
      | ]
      |}
      |""".stripMargin

  private val customsOfficeResponseJsonWithOutPhone: String =
    """
      |{
      | "data" :
      | [
      |    {
      |      "languageCode": "EN",
      |      "name": "CUSTOMS OFFICE SANT JULIÀ DE LÒRIA",
      |      "id": "AD000001",
      |      "countryId": "AD",
      |      "roles": [
      |        {
      |          "role": "AUT"
      |        },
      |        {
      |          "role": "DEP"
      |        },
      |        {
      |          "role": "DES"
      |        },
      |        {
      |          "role": "TRA"
      |        }
      |      ]
      |    },
      |    {
      |      "languageCode": "ES",
      |      "name": "ADUANA DE ST. JULIÀ DE LÒRIA",
      |      "id": "AD000001",
      |      "countryId": "AD",
      |      "roles": [
      |        {
      |          "role": "AUT"
      |        },
      |        {
      |          "role": "DEP"
      |        },
      |        {
      |          "role": "DES"
      |        },
      |        {
      |          "role": "TRA"
      |        }
      |      ]
      |    },
      |    {
      |      "languageCode": "FR",
      |      "name": "BUREAU DE SANT JULIÀ DE LÒRIA",
      |      "id": "AD000001",
      |      "countryId": "AD",
      |      "roles": [
      |        {
      |          "role": "AUT"
      |        },
      |        {
      |          "role": "DEP"
      |        },
      |        {
      |          "role": "DES"
      |        },
      |        {
      |          "role": "TRA"
      |        }
      |      ]
      |    }
      | ]
      |}
      |""".stripMargin

  val countryCodeResponseJson: String =
    """
      |{
      | "data":
      | [
      |  {
      |    "code":"GB",
      |    "state":"valid",
      |    "description":"United Kingdom"
      |  }
      | ]
      |}
      |""".stripMargin

  private val emptyResponseJson: String =
    """
      |{
      |  "data": []
      |}
      |""".stripMargin
}
