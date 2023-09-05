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

package controllers.actions

import models.{DepartureMessageMetaData, LocalReferenceNumber}
import models.DepartureMessageType._
import models.requests.IdentifierRequest
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}
import services.DepartureMessageService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckCancellationStatusProvider @Inject() (departureMessageService: DepartureMessageService)(implicit ec: ExecutionContext) {

  def apply(departureId: String, lrn: LocalReferenceNumber): ActionFilter[IdentifierRequest] =
    new CheckCancellationStatus(departureId, lrn, departureMessageService)
}

class CheckCancellationStatus(
  departureId: String,
  lrn: LocalReferenceNumber,
  departureMessageService: DepartureMessageService
)(implicit protected val executionContext: ExecutionContext)
    extends ActionFilter[IdentifierRequest] {

  override protected def filter[A](request: IdentifierRequest[A]): Future[Option[Result]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    departureMessageService.getMessageMetaDataHead(departureId).map {
      case Some(DepartureMessageMetaData(_, messageType, _)) =>
        messageType match {
          case DepartureNotification | AllocatedMRN | GuaranteeRejected | GoodsUnderControl | DeclarationSent => None
          case _ =>
            Option(Redirect(controllers.routes.CannotSendCancellationRequestController.onPageLoad(departureId, lrn)))
        }
      case _ => Option(Redirect(controllers.routes.ErrorController.technicalDifficulties())) // TODO: If message cant be found redirect to tech diff?
    }
  }

}
