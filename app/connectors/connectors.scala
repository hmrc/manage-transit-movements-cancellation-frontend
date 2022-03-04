/*
 * Copyright 2022 HM Revenue & Customs
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

import connectors.responses.{ConnectorErrorResponse, InvalidStatus, MalformedBody}
import logging.Logging
import models.DepartureId
import play.api.Logger
import play.api.http.Status.OK
import play.api.libs.json.{JsError, JsSuccess, Reads}
import uk.gov.hmrc.http.HttpReads.is2xx
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

package object connectors extends Logging{

  type ConnectorResponse[T] = Either[ConnectorErrorResponse, T]

  implicit val httpReads: HttpReads[HttpResponse] =
    new HttpReads[HttpResponse] {
      override def read(method: String, url: String, response: HttpResponse): HttpResponse =
        response
    }

  def connectorResponseHttpReads[T](departureId: DepartureId, logger: Logger)(implicit reads: Reads[T]): HttpReads[ConnectorResponse[T]] =
    (_: String, url: String, response: HttpResponse) => response.status match {
      case OK => response.json.validate[T] match {
        case JsSuccess(value, _) => Right(value)
        case JsError(_) =>
          logger.error(s"Body cannot be parsed into model, invalid body for url: $url and  departure id: ${departureId.index}")
          Left(MalformedBody)
      }
      case status =>
        logger.warn(s"receive invalid status for url $url and departureId ${departureId.index}")
        Left(InvalidStatus(status))
    }

  def connectorResponseDefaultReads(departureId: DepartureId, logger: Logger): HttpReads[ConnectorResponse[HttpResponse]] =
    (_: String, url: String, response: HttpResponse) => response.status match {
      case status if is2xx(status) => Right(response)
      case status =>
        logger.warn(s"receive invalid status for url $url and departureId ${departureId.index}")
        Left(InvalidStatus(status))
    }

}
