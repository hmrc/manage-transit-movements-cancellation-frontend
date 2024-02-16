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
import models.DepartureMessageType.{AllocatedMRN, DepartureNotification}
import models.{DepartureMessageMetaData, DepartureMessageType}
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DepartureMessageService @Inject() (connectors: DepartureMovementConnector) extends Logging {

  private def getMetaDataByMessageType(
    departureId: String,
    messageType: DepartureMessageType
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[DepartureMessageMetaData]] =
    getMessageMetaData(departureId, messageType)

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

  def getMessageMetaDataHead(departureId: String)(implicit
    ec: ExecutionContext,
    hc: HeaderCarrier
  ): Future[Option[DepartureMessageMetaData]] =
    connectors
      .getMessageMetaData(departureId)
      .map(
        x =>
          x.flatMap(
            _.messages
              .sortBy(_.received)
              .reverse
              .headOption
          )
      )

  def getIE015(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[CC015CType]] =
    (
      for {
        declarationMessage <- OptionT(getMetaDataByMessageType(departureId, DepartureNotification))
        ie015              <- OptionT.liftF(connectors.getMessage[CC015CType](declarationMessage.path))
      } yield ie015
    ).value

  def getIE028(departureId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[CC028CType]] =
    (
      for {
        declarationMessage <- OptionT(getMetaDataByMessageType(departureId, AllocatedMRN))
        ie028              <- OptionT.liftF(connectors.getMessage[CC028CType](declarationMessage.path))
      } yield ie028
    ).value
}
