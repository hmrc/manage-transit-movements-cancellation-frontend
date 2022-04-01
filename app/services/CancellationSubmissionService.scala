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

package services

import connectors.DepartureMovementConnector
import logging.Logging
import models.UserAnswers
import models.messages.CancellationRequest
import pages.CancellationReasonPage
import services.responses.{InvalidState, ServiceErrorResponse}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.TimeMachine

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CancellationSubmissionService @Inject() (
  connector: DepartureMovementConnector,
  timeMachine: TimeMachine
)(implicit ec: ExecutionContext)
    extends Logging {

  def submitCancellation(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Either[ServiceErrorResponse, HttpResponse]] =
    userAnswers
      .get(CancellationReasonPage)
      .map(
        cancellationReason =>
          connector.getMessageSummary(userAnswers.id).flatMap {
            case Right(summary) =>
              summary.messages.get("IE028") match {
                case Some(value) =>
                  connector.getMrnAllocatedMessage(userAnswers.id, value).flatMap {
                    case Right(mrnMessage) =>
                      connector.submitCancellation(userAnswers.id, CancellationRequest(cancellationReason, timeMachine.today(), mrnMessage)).map {
                        case Right(value) => Right(value)
                        case Left(_)      => Left(InvalidState)
                      }
                    case _ => Future.successful(Left(InvalidState))
                  }
                case None =>
                  logger.warn(s"[submitCancellation] no MRNAllocatedMessage found for departureId: ${userAnswers.id.index}")
                  Future.successful(Left(InvalidState))
              }
            case _ => Future.successful(Left(InvalidState))
          }
      )
      .getOrElse {
        logger.warn(s"[submitCancellation] trying to submit cancellation with no cancellation reason page answer for departure id: ${userAnswers.id.index}")
        Future.successful(Left(InvalidState))
      }
}
