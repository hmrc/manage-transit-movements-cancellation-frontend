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
import itbase.{ItSpecBase, WireMockServerHandler}
import models.DepartureId
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse

import scala.xml.NodeSeq

class ApiConnectorSpec extends ItSpecBase with WireMockServerHandler {

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.common-transit-convention-traders.port" -> server.port())

  private lazy val connector: ApiConnector = app.injector.instanceOf[ApiConnector]

  private val url = s"/movements/departures/$departureId/messages"

  "ApiConnector" - {
    "submit" - {
      val body: NodeSeq =
        <ncts:CC014C PhaseID="NCTS5.0" xmlns:ncts="http://ncts.dgtaxud.ec">
          <messageSender>token</messageSender>
        </ncts:CC014C>

      "must return OK for successful response" in {
        server.stubFor(
          post(urlEqualTo(url))
            .withRequestBody(equalTo(body.toString()))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.1+json"))
            .withHeader("Content-Type", equalTo("application/xml"))
            .willReturn(aResponse().withStatus(OK))
        )

        val result: HttpResponse = connector.submit(body, DepartureId(departureId)).futureValue

        result.status mustBe OK
      }
    }
  }
}
