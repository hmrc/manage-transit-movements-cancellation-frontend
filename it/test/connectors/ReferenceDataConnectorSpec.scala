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

import com.github.tomakehurst.wiremock.client.WireMock.*
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import connectors.ReferenceDataConnectorSpec.*
import itbase.{ItSpecBase, WireMockServerHandler}
import models.CustomsOffice
import org.scalacheck.Gen
import org.scalatest.{Assertion, EitherValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends ItSpecBase with WireMockServerHandler with EitherValues {

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  private val baseUrl = "customs-reference-data/test-only"

  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.customs-reference-data.port" -> server.port())

  "Reference Data" - {
    "getCustomsOffice" - {
        val code = "XI000014"
        val url  = s"/$baseUrl/lists/CustomsOffices?referenceNumbers=$code"

        val customsOfficeResponseJson: String =
          """
            |[
            |  {
            |    "customsOfficeLsd": {
            |      "languageCode": "EN",
            |      "customsOfficeUsualName": "Glasgow Airport"
            |    },
            |    "phoneNumber": "+44(0)300 106 3520",
            |    "referenceNumber": "GB000054",
            |    "countryCode": "GB"
            |  },
            |  {
            |    "customsOfficeLsd": {
            |      "languageCode": "EN",
            |      "customsOfficeUsualName": "Belfast International Airport"
            |    },
            |    "phoneNumber": "+44 (0)3000 575 988",
            |    "referenceNumber": "XI000014",
            |    "countryCode": "XI"
            |  }
            |]
            |""".stripMargin

        "should handle a 200 response for customs office with code end point" in {
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(customsOfficeResponseJson))
              )

              val expectedResult = CustomsOffice("XI000014", "Belfast International Airport", "XI", Some("+44 (0)3000 575 988"))

              connector.getCustomsOffice(code).futureValue.value mustEqual expectedResult
        }

        "should throw a NoReferenceDataFoundException for an empty response" in {
          checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getCustomsOffice(code))
        }

        "should handle client and server errors for customs office end point" in {
          checkErrorResponse(url, connector.getCustomsOffice(code))
        }
    }
  }

  private def checkNoReferenceDataFoundResponse(url: String, json: String, result: => Future[Either[Exception, ?]]): Assertion = {
    server.stubFor(
      get(urlEqualTo(url))
        .willReturn(okJson(json))
    )

    result.futureValue.left.value mustBe a[NoReferenceDataFoundException]
  }

  private def checkErrorResponse(url: String, result: => Future[Either[Exception, ?]]): Assertion = {
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

        result.futureValue.left.value mustBe an[Exception]
    }
  }
}

object ReferenceDataConnectorSpec {

  private val emptyPhase5ResponseJson: String =
    """
      |{
      |  "data": []
      |}
      |""".stripMargin

  private val emptyPhase6ResponseJson: String =
    """
      |[]
      |""".stripMargin
}
