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

package services

import cats.data.OptionT
import connectors.DepartureMovementConnector
import generated._
import models.MessageType._
import models.{MessageMetaData, MessageType}
import play.api.Logging
import scalaxb.XMLFormat
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DepartureMessageService @Inject() (connectors: DepartureMovementConnector) extends Logging {

  private def getMessageMetaData(
    departureId: String,
    messageType: Option[MessageType]
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[MessageMetaData]] =
    connectors
      .getMessageMetaData(departureId)
      .map {
        _.flatMap {
          _.messages
            .filter {
              message => messageType.fold(true)(_ == message.messageType)
            }
            .sortBy(_.received)
            .reverse
            .headOption
        }
      }

  def getMessageMetaDataHead(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[MessageMetaData]] =
    getMessageMetaData(departureId, None)

  def getIE014(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[CC014CType]] =
    getMessage[CC014CType](departureId, DeclarationInvalidationRequest)

  def getIE015(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[CC015CType]] =
    getMessage[CC015CType](departureId, DepartureNotification)

  def getIE028(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[CC028CType]] =
    getMessage[CC028CType](departureId, AllocatedMRN)

  def getMessage[T](
    departureId: String,
    messageType: MessageType
  )(implicit ec: ExecutionContext, hc: HeaderCarrier, format: XMLFormat[T]): Future[Option[T]] = (
    for {
      metaData <- OptionT(getMessageMetaData(departureId, Some(messageType)))
      message  <- OptionT.liftF(connectors.getMessage[T](departureId, metaData.id))
    } yield message
  ).value
}
