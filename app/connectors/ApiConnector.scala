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
import play.api.Logging
import play.api.http.HeaderNames.*
import play.api.libs.ws.XMLBodyWritables.*
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpErrorFunctions, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

class ApiConnector @Inject() (
  http: HttpClientV2,
  appConfig: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends HttpErrorFunctions
    with Logging {

  private val version = 2.1

  def submit(xml: NodeSeq, departureId: DepartureId)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val url = url"${appConfig.commonTransitConventionTradersUrl}movements/departures/${departureId.value}/messages"
    http
      .post(url)
      .setHeader(
        ACCEPT       -> s"application/vnd.hmrc.$version+json",
        CONTENT_TYPE -> "application/xml"
      )
      .withBody(xml)
      .execute[HttpResponse]
  }
}
