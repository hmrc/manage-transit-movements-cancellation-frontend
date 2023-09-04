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

import config.FrontendAppConfig
import models.DepartureId
import models.messages.IE014Data
import play.api.Logging
import play.api.http.HeaderNames
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpErrorFunctions, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApiConnector @Inject() (httpClient: HttpClient, appConfig: FrontendAppConfig)(implicit ec: ExecutionContext) extends HttpErrorFunctions with Logging {

  private val requestHeaders = Seq(
    HeaderNames.ACCEPT       -> "application/vnd.hmrc.2.0+json",
    HeaderNames.CONTENT_TYPE -> "application/json"
  )

  def submit(ie014Data: IE014Data, departureId: DepartureId)(implicit hc: HeaderCarrier): Future[Boolean] = {

    val serviceUrl = s"${appConfig.commonTransitConventionTradersUrl}movements/departures/${departureId.value}/messages"
    httpClient
      .POST(serviceUrl, ie014Data, requestHeaders)
      .map {
        response =>
          response.status match {
            case code if is2xx(code) => true
            case _ =>
              logger.info(s"ApiConnector:submit: failed: ${response.status}-${response.body}")
              false
          }
      }
  }
}
