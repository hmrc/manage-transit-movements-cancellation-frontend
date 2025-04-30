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
import generated.*
import models.MessageType.*
import models.{IE015, IE028, MessageMetaData, MessageStatus, MessageType}
import play.api.Logging
import scalaxb.`package`.fromXML
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.Node

class DepartureMessageService @Inject() (connectors: DepartureMovementConnector) extends Logging {

  private def getMessageMetaData(
    departureId: String,
    messageType: Option[MessageType]
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[MessageMetaData]] =
    connectors
      .getMessageMetaData(departureId)
      .map {
        _.messages
          .filter {
            message => messageType.fold(true)(_ == message.messageType)
          }
          .filterNot(_.status == MessageStatus.Failed)
          .sorted
          .headOption
      }

  def getMessageMetaDataHead(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[MessageMetaData]] =
    getMessageMetaData(departureId, None)

  def getIE014(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[CC014CType]] = {
    implicit val format: Node => CC014CType = fromXML[CC014CType](_)
    getMessage(departureId, DeclarationInvalidationRequest)
  }

  def getIE015(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[IE015]] =
    getMessage(departureId, DepartureNotification)

  def getIE028(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[IE028]] =
    getMessage(departureId, AllocatedMRN)

  private def getMessage[T](
    departureId: String,
    messageType: MessageType
  )(implicit ec: ExecutionContext, hc: HeaderCarrier, format: Node => T): Future[Option[T]] = (
    for {
      metaData <- OptionT(getMessageMetaData(departureId, Some(messageType)))
      message  <- OptionT.liftF(connectors.getMessage(departureId, metaData.id))
    } yield message
  ).value
}
