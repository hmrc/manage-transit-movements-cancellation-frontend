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
import models.messages.{IE015Data, IE028Data}
import models.{DepartureMessages, LocalReferenceNumber}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}
import uk.gov.hmrc.http.HttpReads.Implicits._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DepartureMovementConnector @Inject() (val appConfig: FrontendAppConfig, http: HttpClient)(implicit ec: ExecutionContext) extends Logging {

  def getLRN(location: String)(implicit hc: HeaderCarrier): Future[LocalReferenceNumber] = {

    val headers = hc.withExtraHeaders(("Accept", "application/vnd.hmrc.2.0+json"))

    val url = s"${appConfig.commonTransitConventionTradersUrl}$location"

    http.GET[LocalReferenceNumber](url)(HttpReads[LocalReferenceNumber], headers, ec)
  }

  def getIE015(location: String)(implicit hc: HeaderCarrier): Future[IE015Data] = {

    val headers = hc.withExtraHeaders(("Accept", "application/vnd.hmrc.2.0+json"))

    val url = s"${appConfig.commonTransitConventionTradersUrl}$location"

    http.GET[IE015Data](url)(HttpReads[IE015Data], headers, ec)
  }

  def getIE028(location: String)(implicit hc: HeaderCarrier): Future[IE028Data] = {

    val headers = hc.withExtraHeaders(("Accept", "application/vnd.hmrc.2.0+json"))

    val url = s"${appConfig.commonTransitConventionTradersUrl}$location"

    http.GET[IE028Data](url)(HttpReads[IE028Data], headers, ec)
  }

  def getMessageMetaData(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[DepartureMessages]] = {
    val headers = hc.withExtraHeaders(("Accept", "application/vnd.hmrc.2.0+json"))

    val serviceUrl = s"${appConfig.commonTransitConventionTradersUrl}movements/departures/$departureId/messages"
    http.GET[Option[DepartureMessages]](serviceUrl)(implicitly, headers, ec) recover {
      case exception =>
        logger.warn("getMessageMetaData failed with exception", exception)
        None
    }
  }
}
