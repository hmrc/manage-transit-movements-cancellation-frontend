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
import models.DepartureMessages
import scalaxb.XMLFormat
import scalaxb.`package`.fromXML
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.XML

class DepartureMovementConnector @Inject() (val appConfig: FrontendAppConfig, http: HttpClient)(implicit ec: ExecutionContext) extends Logging {

  def getMessage[T](path: String)(implicit hc: HeaderCarrier, format: XMLFormat[T]): Future[T] = {
    val headers = hc.withExtraHeaders(("Accept", "application/vnd.hmrc.2.0+xml"))

    val url = s"${appConfig.commonTransitConventionTradersUrl}$path/body"

    http.GET[HttpResponse](url)(readRaw, headers, ec).map(_.body).map {
      xml => fromXML(XML.loadString(xml))
    }
  }

  def getMessageMetaData(departureId: String)(implicit hc: HeaderCarrier): Future[Option[DepartureMessages]] = {
    val headers = hc.withExtraHeaders(("Accept", "application/vnd.hmrc.2.0+json"))

    val url = s"${appConfig.commonTransitConventionTradersUrl}movements/departures/$departureId/messages"
    http.GET[Option[DepartureMessages]](url)(implicitly, headers, ec) recover {
      case exception =>
        logger.warn("getMessageMetaData failed with exception", exception)
        None
    }
  }
}
