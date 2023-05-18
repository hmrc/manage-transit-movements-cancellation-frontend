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
import logging.Logging
import models.{DepartureMessages, LocalReferenceNumber}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DepartureMovementConnector @Inject() (val appConfig: FrontendAppConfig, http: HttpClient)(implicit ec: ExecutionContext) extends Logging {

  def getLRN(location: String)(implicit hc: HeaderCarrier): Future[LocalReferenceNumber] = {

    val headers = hc.withExtraHeaders(("Accept", "application/vnd.hmrc.2.0+json"))

    val url = s"${appConfig.commonTransitConventionTradersUrl}$location"

    http.GET[LocalReferenceNumber](url)(HttpReads[LocalReferenceNumber], headers, ec)
  }

  def getMessageMetaData(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[DepartureMessages] = {
    val headers = hc.withExtraHeaders(("Accept", "application/vnd.hmrc.2.0+json"))

    val serviceUrl = s"${appConfig.commonTransitConventionTradersUrl}movements/departures/$departureId/messages"
    http.GET[DepartureMessages](serviceUrl)(implicitly, headers, ec)
  }
}
