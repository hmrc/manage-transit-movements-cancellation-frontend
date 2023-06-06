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

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import generators.Generators
import models.DepartureId
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.mvc.Results.{BadRequest, InternalServerError}
import play.api.test.Helpers.{await, _}
import uk.gov.hmrc.http.{BadRequestException, HttpResponse}

import scala.concurrent.Future

class ApiConnectorSpec extends SpecBase with WireMockSuite with Generators {

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.common-transit-convention-traders.port" -> server.port())

  private lazy val connector: ApiConnector = app.injector.instanceOf[ApiConnector]

  val uri = s"/movements/departures/$departureId/messages"

  private def checkErrorResponse(url: String, result: => Future[_]): Assertion = {
    val errorResponses: Gen[Int] = Gen
      .chooseNum(400: Int, 599: Int)
      .suchThat(_ != 404)

    //forAll(errorResponses) {
    //errorResponse =>
    server.stubFor(
      get(urlEqualTo(url))
        .willReturn(
          aResponse()
            .withStatus(BAD_REQUEST)
        )
    )

    whenReady(result.failed) {
      _ mustBe an[Exception]
    }
    //}
  }

  "ApiConnector" - {

    "submit" - {

      "return successful response" in {

        server.stubFor(
          post(uri)
            .withHeader("Accept", containing("application/vnd.hmrc.2.0+json"))
            .willReturn(ok())
        )

        val res: Either[Result, HttpResponse] = await(connector.submit(ie014Data, DepartureId(departureId)))
        res.toString mustBe Right(HttpResponse(OK, "")).toString
      }

      "return badrequest response" in {

        server.stubFor(
          post(uri)
            .withHeader("Accept", containing("application/vnd.hmrc.2.0+json"))
            .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
        )

        val res: Either[Result, HttpResponse] = await(connector.submit(ie014Data, DepartureId(departureId)))
        res.toString mustBe Left(InternalServerError("ApiConnector:submit: failed with exception: Remotely closed")).toString
      }

    }
  }

}
