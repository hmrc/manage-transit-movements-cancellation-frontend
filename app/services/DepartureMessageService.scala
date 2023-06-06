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
import models.DepartureMessageType.DepartureNotification
import models.messages.IE015Data
import models.{DepartureMessageMetaData, DepartureMessageType, LocalReferenceNumber}
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DepartureMessageService @Inject() (connectors: DepartureMovementConnector) extends Logging {

  private def getDepartureNotificationMetaData(
    departureId: String
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[DepartureMessageMetaData]] =
    getMessageMetaData(departureId, DepartureNotification)

  private def getMessageMetaData(departureId: String, messageType: DepartureMessageType)(implicit
    ec: ExecutionContext,
    hc: HeaderCarrier
  ): Future[Option[DepartureMessageMetaData]] =
    connectors
      .getMessageMetaData(departureId)
      .map(
        x =>
          x.flatMap(
            _.messages
              .filter(_.messageType == messageType)
              .sortBy(_.received)
              .reverse
              .headOption
          )
      )

  def getLRNFromDeclarationMessage(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[LocalReferenceNumber]] =
    (
      for {
        declarationMessage <- OptionT(getDepartureNotificationMetaData(departureId))
        lrn                <- OptionT(connectors.getLRN(declarationMessage.path))
      } yield lrn
    ).value

  def getIE015FromDeclarationMessage(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[IE015Data]] =
    (
      for {
        declarationMessage <- OptionT(getDepartureNotificationMetaData(departureId))
        ie015              <- OptionT(connectors.getIE015(declarationMessage.path))
      } yield ie015
    ).value
}
